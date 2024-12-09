package com.aneesh.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import java.util.Scanner;

public class ShopifyHelper {

    private static final String shopifyDomain = SecretsHelper.getProperty("shopify.domain");
    private static final String shopifyApiKey = SecretsHelper.getProperty("shopify.apiKey");

    public static JSONObject getOrderFromShopify(Scanner scanner) {
        // Part 1: Fetch orders from Shopify
        Long customerId = getCustomerIdAsInput(scanner);

        JSONArray draftOrders = getAllOrders();
        if (draftOrders == null) {
            System.out.println("No orders available.. exiting");
            return null;
        }

        JSONArray customerOrders = extractCustomerOrders(draftOrders, customerId);

        if (customerOrders.isEmpty()) {
            System.out.println("No orders found for customer.. exiting");
            return null;
        }

        System.out.println("Here are your orders from Shopify:");
        for (int i = 0; i < customerOrders.length(); i++) {
            printOrderDetails(customerOrders, i);
        }

        Integer orderNumber = getOrderNumberForSupportTicket(scanner, customerOrders);
        if (orderNumber == null) {
            System.out.println("Order number is null... exiting");
            return null;
        }

        return customerOrders.getJSONObject(orderNumber - 1); // Return the selected order
    }

    private static Integer getOrderNumberForSupportTicket(Scanner scanner, JSONArray customerOrders) {
        System.out.print("\nEnter your order #: ");
        int orderNumber = scanner.nextInt();
        if (orderNumber < 1 || orderNumber > customerOrders.length()) {
            System.out.println("Invalid order number. Please enter a valid number.");
            return null;
        }
        return orderNumber;
    }

    private static void printOrderDetails(JSONArray customerOrders, int i) {
        JSONObject order = customerOrders.getJSONObject(i);
        JSONArray items = order.getJSONArray("line_items");
        StringBuilder itemNames = new StringBuilder();
        for (int j = 0; j < items.length(); j++) {
            itemNames.append(items.getJSONObject(j).getString("name"));
            if (j < items.length() - 1) {
                itemNames.append(", ");
            }
        }
        System.out.println((i + 1) + ". Order #" + (i + 1) + "\n   Items: " + itemNames);
    }

    private static JSONArray extractCustomerOrders(JSONArray draftOrders, Long customerId) {
        System.out.println("Here are your orders from Shopify:");
        JSONArray customerOrders = new JSONArray();
        for (int i = 0; i < draftOrders.length(); i++) {
            JSONObject order = draftOrders.getJSONObject(i);
            try {
                if (order.has("customer")
                        && order.getJSONObject("customer").has("default_address")
                        && order.getJSONObject("customer").getJSONObject("default_address").has("customer_id")
                ) {
                    long customerIdOfOrder = order.getJSONObject("customer").getJSONObject("default_address").getLong("customer_id");
                    if (customerIdOfOrder == customerId) {
                        customerOrders.put(order);
                    }
                }
            } catch (Exception e) {
                System.out.println("something wrong happened in finding customerId");
            }
        }
        return customerOrders;
    }

    private static JSONArray getAllOrders() {
        OkHttpClient client = new OkHttpClient();

        // Create a request using the properties
        Request request = new Request.Builder()
                .url(shopifyDomain)
                .header("X-Shopify-Access-Token", shopifyApiKey)
                .build();

        // Execute the request
        JSONArray draftOrders = null;
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                draftOrders = new JSONObject(responseBody).getJSONArray("draft_orders");
            } else {
                System.out.println("Request failed " + response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return draftOrders;
    }

    private static Long getCustomerIdAsInput(Scanner scanner) {
        System.out.print("Enter your customer ID: ");
        Long customerId = scanner.nextLong();
        scanner.nextLine(); // Consume newline
        return customerId;
    }
}
