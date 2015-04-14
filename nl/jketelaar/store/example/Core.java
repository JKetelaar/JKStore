package nl.jketelaar.store;

import nl.jketelaar.store.domains.StoreItem;
import nl.jketelaar.store.methods.Store;
import org.parabot.core.Context;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.LoopTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JKetelaar
 */
@ScriptManifest(author = "JKetelaar",
        category = Category.OTHER,
        description = "Indexes the player stores",
        name = "PBStore",
        servers = { "Ikov" },
        version = 1.3)
public class Core extends Script implements LoopTask{

    private final List<StoreItem> itemList = new ArrayList<>();

    @Override
    public boolean onExecute() {
        itemList.add(new StoreItem("Air rune", 556));
        itemList.add(new StoreItem("Rocktail", 15272));
        itemList.add(new StoreItem("Shark", 385));
        return true;
    }

    @Override
    public int loop() {
        List<StoreItem> storeItems = new Store().getPrices(itemList);
        for (StoreItem storeItem : storeItems){
            int total = 0;
            for (String price : storeItem.getPrices()){
                total += Integer.parseInt(price);
            }
            if (total > 0){
                System.out.println(storeItem.getName() + " has an average price of " + (total / storeItem.getPrices().size()));
            }
        }
        Context.getInstance().getRunningScript().setState(STATE_STOPPED);
        Time.sleep(500);
        return 1;
    }
}
