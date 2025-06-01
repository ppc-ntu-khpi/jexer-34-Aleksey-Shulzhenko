package com.mybank.tui;

import jexer.*;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;

import java.io.*;
import java.util.List;

class Account implements Serializable {
    protected double balance;

    public Account(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountType() {
        return "Generic";
    }
}

class CheckingAccount extends Account {
    public CheckingAccount(double balance) {
        super(balance);
    }

    @Override
    public String getAccountType() {
        return "Checking";
    }
}

class SavingsAccount extends Account {
    public SavingsAccount(double balance) {
        super(balance);
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }
}

class Customer implements Serializable {
    private String firstName;
    private String lastName;
    private List<Account> accounts;

    public Customer(String f, String l, List<Account> accs) {
        this.firstName = f;
        this.lastName = l;
        this.accounts = accs;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}

public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    private List<Customer> customers;

    public static void main(String[] args) throws Exception {
        TUIdemo app = new TUIdemo();
        (new Thread(app)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();

        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);

        addWindowMenu();

        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");

        setFocusFollowsMouse(true);

        // Укажи СВОЙ путь к файлу!
       loadCustomersFromFile("test/test.dat");

        ShowCustomerDetails();
    }

    private void loadCustomersFromFile(String filepath) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            customers = (List<Customer>) in.readObject();
        } catch (Exception e) {
            customers = null;
            System.out.println("Ошибка загрузки: " + e.getMessage());
        }
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "Simple Jexer Demo\nAuthor: Taurus Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        TWindow custWin = addWindow("Customer Info", 2, 1, 50, 12, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter customer number and press Show...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        TField custNo = custWin.addField(28, 2, 5, false);
        TText details = custWin.addText("", 2, 4, 46, 6);

        custWin.addButton("&Show", 35, 2, new TAction() {
            @Override
            public void DO() {
                try {
                    int custId = Integer.parseInt(custNo.getText());

                    if (customers == null) {
                        details.setText("No data loaded.");
                        return;
                    }

                    if (custId < 0 || custId >= customers.size()) {
                        details.setText("Customer ID out of bounds!");
                        return;
                    }

                    Customer c = customers.get(custId);
                    Account acc = c.getAccounts().get(0); // первый аккаунт

                    String text = "Owner Name: " + c.getFirstName() + " " + c.getLastName() + "\n" +
                                  "Account Type: " + acc.getAccountType() + "\n" +
                                  "Account Balance: $" + acc.getBalance();
                    details.setText(text);
                } catch (Exception e) {
                    details.setText("Invalid input!");
                }
            }
        });
    }
}
