//to implement: order multiple items in a single order(use Arraylist)
package Common_systems.Food_delivery;

import java.util.*;

public class fooddelivery {
    public static void main(String[] args) {
        orders o = new orders();
        Scanner s = new Scanner(System.in);
        System.out.println("Enter number of restaurants");
        int n = s.nextInt();
        s.nextLine();
        for (int i = 0; i < n; i++) {
            System.out.println("enter name:");
            String name = s.nextLine();

            System.out.println("enter id:");
            int id = s.nextInt();
            s.nextLine();

            restaurant r = new restaurant(id, name);
            System.out.println("Enter number items on menu");
            int m = s.nextInt();
            s.nextLine();
            for (int j = 0; j < m; j++) {
                System.out.println("enter name of item");
                String item = s.nextLine();

                System.out.println("enter price");
                int price = s.nextInt();
                s.nextLine();
                r.additems(item, price);
            }
            o.addres(r);
        }
        while (true) {
            System.out.println("enter username");
            String name = s.nextLine();

            System.out.println("enter userid");

            int id = s.nextInt();
            s.nextLine();
            if (id == -1)
                break;
            user u = new user(id, name);
            System.out.println("Select restaurant to order from");
            o.showres();
            String str = s.nextLine();
            restaurant req = o.getresbyname(str);
            req.showmenu();
            System.out.println("Enter items to order from menu");

            String orders = s.nextLine();
            String[] it = orders.split(",");
            ArrayList<String> orderarray = new ArrayList<>(Arrays.asList(it));

            for (String singleorder : orderarray) {
                if (req.getmenu().containsKey(singleorder) == false) {
                    System.out.println("Item not in menu");
                }
                u.addtocart(singleorder, req.getmenu());
            }
            payment p = new payment("Gpay", u.price);
            u.pay(p, u.price);
            o.place_order(u, req, p);

            track t = new track(u, o);

            // o.changestatus(orderStatus.recieved);

            if (t.trackorder() == orderStatus.ordered) {
                System.out.println("Your order has been recieved and being prepared");
            } else if (t.trackorder() == orderStatus.recieved) {
                System.out.println("Thank you for ordering");
            }

            o.reset_order();
            p.resetpayment();

        }

        s.close();
    }

}

enum orderStatus {
    ordering,
    ordered,
    recieved,
    returned
};

enum paymentStatus {
    pending,
    completed,
    failed
}

class track {
    private orders o;
    private user u;

    track(user u, orders o) {
        this.o = o;
        this.u = u;
    }

    public orderStatus trackorder() {
        if (o.getCurrent_orders().containsKey(u)) {
            return o.getCurrent_orders().get(u) == paymentStatus.completed ? o.getOrderStatus() : null;
        }
        return null;
    }

}

class user {
    private int id;
    private String name;
    private ArrayList<String> items = new ArrayList<>();
    int price;

    user(int id, String name) {
        this.id = id;
        this.name = name;
        this.price = 0;
    }

    public void addtocart(String fooditem, HashMap<String, Integer> menu) {
        items.add(fooditem);
        price = price + menu.get(fooditem);
    }

    public void pay(payment p, int amount) {
        if (amount == p.getPrice()) {
            System.out.println("Paid");

            p.updatePaymentStatus(paymentStatus.completed);
        } else {
            System.out.println("Couldnt complete transaction");
            p.updatePaymentStatus(paymentStatus.failed);

        }
    }

    public ArrayList<String> getitems() {
        return items;
    }

}

class restaurant {
    private int id;
    private String name;
    private HashMap<String, Integer> menu = new HashMap<>();

    restaurant(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void additems(String item, int price) {
        menu.put(item, price);
    }

    public HashMap<String, Integer> getmenu() {
        return menu;
    }

    public void showmenu() {
        System.out.println("Menu:");
        for (String s : menu.keySet()) {
            System.out.println(s + ":" + menu.get(s));
        }
    }

    public String getname() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}

// class deliveryperson {

// }

class orders {
    private int id;
    private orderStatus os;
    private ArrayList<restaurant> restaurants = new ArrayList<>();
    private HashMap<String, restaurant> nameMap = new HashMap<>();
    private HashMap<user, paymentStatus> current_orders = new HashMap<>();

    orders() {
        this.os = orderStatus.ordering;
    }

    public void addres(restaurant r) {
        restaurants.add(r);
        nameMap.put(r.getname(), r);
    }

    public void showres() {
        for (restaurant r : restaurants) {
            System.out.println(r);
        }
    }

    public restaurant getresbyname(String n) {
        for (String s : nameMap.keySet()) {
            if (s.equals(n)) {
                return nameMap.get(s);
            }
        }
        return null;
    }

    public HashMap<user, paymentStatus> getCurrent_orders() {
        return current_orders;
    }

    public orderStatus getOrderStatus() {
        return os;
    }

    public void place_order(user u, restaurant r, payment p) {

        ;
        if (p.getPaymentStatus() == paymentStatus.completed) {
            System.out.println("congratulations your order has been placed");
            System.out.println("Total Payment:" + u.price);
            System.out.println("Your items:");
            for (String s : u.getitems()) {
                System.out.println(s);
            }
            this.id = (int) (Math.random());
            this.os = orderStatus.ordered;
            current_orders.put(u, p.getPaymentStatus());
        } else {
            System.out.println("please pay using " + p.gettype());
            System.out.println("Your order could not be placed");
        }
    }

    public void changestatus(orderStatus s) {
        os = s;
    }

    public void reset_order() {
        os = orderStatus.ordering;

    }

}

class payment {
    private paymentStatus ps;
    private String type;
    private int price;

    payment(String s, int price) {
        this.type = s;
        this.price = price;
        this.ps = paymentStatus.pending;
    }

    public void resetpayment() {
        ps = paymentStatus.pending;
    }

    public String gettype() {
        return type;
    }

    public paymentStatus getPaymentStatus() {
        return ps;
    }

    public void updatePaymentStatus(paymentStatus st) {
        ps = st;
    }

    public int getPrice() {
        return price;
    }
}