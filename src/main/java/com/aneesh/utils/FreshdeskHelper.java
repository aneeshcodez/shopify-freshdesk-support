package com.aneesh.utils;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

import java.util.Scanner;

public class FreshdeskHelper {
    private static final String freshdeskDomain = SecretsHelper.getProperty("freshdesk.domain");
    private static final String freshdeskApiKey = SecretsHelper.getProperty("freshdesk.apiKey");


    public static void createFreshdeskTicket(Scanner scanner, JSONObject selectedOrder) {
        // Part 2: Create a Freshdesk ticket for the selected order
        String issue = getCustomerIssueAsInput(scanner);

        String freshdeskUrl = "https://" + freshdeskDomain + "/api/v2/tickets";

        JSONObject ticketDetails = constructTicketDetail(selectedOrder, issue);

        StringBuilder ticketResponse = createTicketUsingApi(freshdeskUrl, freshdeskApiKey, ticketDetails);
        if (ticketResponse == null) {
            System.out.println("Ticket response is null. exiting...");
            return;
        }

        printTicketResponse(ticketResponse);
    }

    private static void printTicketResponse(StringBuilder ticketResponse) {
        JSONObject ticketResponseJson = new JSONObject(ticketResponse.toString());
        int ticketId = ticketResponseJson.getInt("id");

        System.out.println("Ticket created successfully!");
        System.out.println("Ticket ID: " + ticketId);
    }

    private static String getCustomerIssueAsInput(Scanner scanner) {
        System.out.print("\nEnter the issue to create a Freshdesk ticket: ");
        scanner.nextLine(); // Consume newline
        String issue = scanner.nextLine();
        return issue;
    }

    private static StringBuilder createTicketUsingApi(String freshdeskUrl, String freshdeskApiKey, JSONObject ticketDetails) {
        OkHttpClient client = new OkHttpClient();

        // Request body
        MediaType jsonMediatype = MediaType.get("application/json");

        RequestBody body = RequestBody.create(ticketDetails.toString(), jsonMediatype);

        String username = freshdeskApiKey;
        String password = "X";
        String credential = Credentials.basic(username, password);

        // Create the request
        Request request = new Request.Builder()
                .url(freshdeskUrl)
                .post(body)  // POST method
                .header("Authorization", credential)
                .build();

        // Execute the request
        StringBuilder responseString = new StringBuilder();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                responseString.append(response.body().string());
            } else {
                System.out.println("Request failed: " + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString.length() > 0 ? responseString : null;
    }

    private static JSONObject constructTicketDetail(JSONObject selectedOrder, String issue) {
        JSONObject ticketDetails = new JSONObject();
        ticketDetails.put("description", "Issue: " + issue
                + "\nOrder:" + selectedOrder);
        ticketDetails.put("subject", "OrderId: " + selectedOrder.getLong("id"));
        ticketDetails.put("email", "customer@example.com");
        ticketDetails.put("status", 2);
        ticketDetails.put("priority", 1);
        return ticketDetails;
    }
}
