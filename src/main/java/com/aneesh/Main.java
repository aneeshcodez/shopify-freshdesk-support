package com.aneesh;

import java.util.Scanner;

import com.aneesh.utils.FreshdeskHelper;
import com.aneesh.utils.ShopifyHelper;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        JSONObject selectedOrder = ShopifyHelper.getOrderFromShopify(scanner);

        if (selectedOrder == null) {
            System.out.println("No order found. exiting..");

            return;
        }

        FreshdeskHelper.createFreshdeskTicket(scanner, selectedOrder);
    }

}
