package org.example.entities;

import org.example.interfaces.Catalogable;
import org.example.interfaces.Sellable;

public class DigitalVideoGame extends VideoGame implements Sellable, Catalogable {

    private double sizeGB;
    private String downloadPlatform;

    public DigitalVideoGame(String title, double price, String platform, int stock, String genre, double sizeGB, String downloadPlatform) {

        super(title, price, platform, stock, genre);
        this.sizeGB = sizeGB;
        this.downloadPlatform = downloadPlatform;
    }

    public double getSizeGB(){
        return sizeGB;
    }

    public String getDownloadPlatform(){
        return downloadPlatform;
    }
    public void   setSizeGB(double sizeGB){
        this.sizeGB = sizeGB;
    }

    public void   setDownloadPlatform(String dp){
        this.downloadPlatform = dp;
    }

    @Override
    public double calculateFinalPrice() {

        return sizeGB > 50 ? price + 5000 : price;
    }

    @Override
    public double sell(int qty) {

        return calculateFinalPrice() * qty;
    }

    @Override
    public String getDisplayInfo() {
        return "Digital | " + title + " | " + platform + " | $" + calculateFinalPrice() + " | Stock: " + stock;
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{title, platform, genre, calculateFinalPrice(), stock};
    }

    @Override
    public String toString() {
        return "DigitalVideoGame{title='" + title + "', price=" + price + ", platform='" + platform + "', stock=" + stock + ", genre='" + genre + "', sizeGB=" + sizeGB + ", downloadPlatform='" + downloadPlatform + "'}";
    }
}
