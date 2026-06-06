package org.example.entities;

import org.example.interfaces.Catalogable;
import org.example.interfaces.Sellable;

public class PhysicalVideoGame extends VideoGame implements Sellable, Catalogable {

    private String condition;
    private String distributor;

    public PhysicalVideoGame(String title, double price, String platform, int stock, String genre, String condition, String distributor) {

        super(title, price, platform, stock, genre);
        this.condition   = condition;
        this.distributor = distributor;

    }


    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    @Override
    public double calculateFinalPrice() {
        return "usado".equalsIgnoreCase(condition) ? price * 0.75 : price;
    }

    @Override
    public double sell(int qty) {
        return calculateFinalPrice() * qty;
    }

    @Override
    public String getDisplayInfo() {
        return "Físico | " + title + " | " + platform +
               " | $" + calculateFinalPrice() + " | Stock: " + stock;
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{title, platform, genre, calculateFinalPrice(), stock};
    }

    @Override
    public String toString() {
        return "PhysicalVideoGame{title='" + title + "', price=" + price +
               ", platform='" + platform + "', stock=" + stock +
               ", genre='" + genre + "', condition='" + condition +
               "', distributor='" + distributor + "'}";
    }
}
