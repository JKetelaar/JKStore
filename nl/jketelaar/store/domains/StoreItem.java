package nl.jketelaar.store.domains;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JKetelaar
 */
public class StoreItem {

    private final String name;
    private final int id;
    private final List<String> prices;

    public StoreItem(String name, int id) {
        this.name = name;
        this.id = id;
        prices = new ArrayList<>();
    }

    public void addPrice(String price){
        this.prices.add(price);
    }

    public int getId() {
        return id;
    }

    public List<String> getPrices() {
        return prices;
    }

    public String getName() {
        return name;
    }
}
