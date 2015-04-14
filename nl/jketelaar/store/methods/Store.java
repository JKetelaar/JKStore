package nl.jketelaar.store.methods;

import nl.jketelaar.store.data.Constants;
import nl.jketelaar.store.domains.StoreItem;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.input.Keyboard;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.rev317.min.Loader;
import org.rev317.min.accessors.Interface;
import org.rev317.min.api.events.MessageEvent;
import org.rev317.min.api.events.listeners.MessageListener;
import org.rev317.min.api.methods.Game;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.wrappers.Item;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.script.ScriptEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JKetelaar
 */
public class Store implements MessageListener{

    private StoreItem currentItem;
    private boolean received = false;
    private boolean listen;

    public Store(){
        ScriptEngine.getInstance().addMessageListener(this);
    }

    public List<StoreItem> getPrices(List<StoreItem> storeItems){
        listen = true;
        Npc[] traders;
        if ((traders = Npcs.getNearest(2127)) != null && traders.length >= 1) {
            for (StoreItem storeItem : storeItems) {
                currentItem = storeItem;
                for (int i = 0; i < 3; i++) {
                    received = false;
                    Npc trader = traders[0];
                    if (trader != null) {
                        trader.interact(2);
                        Time.sleep(new SleepCondition() {
                            @Override
                            public boolean isValid() {
                                return Game.getOpenInterfaceId() == Constants.SEARCH_ID;
                            }
                        }, 5000);
                        Time.sleep(500);
                        if (Game.getOpenInterfaceId() == Constants.SEARCH_ID) {
                            Menu.sendAction(315, 0, 0, 41463);
                            Time.sleep(new SleepCondition() {
                                @Override
                                public boolean isValid() {
                                    return Game.getOpenBackDialogId() > 0;
                                }
                            }, 500);
                            Keyboard.getInstance().sendKeys(storeItem.getName());
                            Time.sleep(new SleepCondition() {
                                @Override
                                public boolean isValid() {
                                    String message;
                                    return (message = Loader.getClient().getInterfaceCache()[41472].getMessage()) != null && message.length() > 0;
                                }
                            }, 5000);
                            int start = 41474;
                            Menu.sendAction(646, 0, 0, start + (i * 4));
                            Time.sleep(new SleepCondition() {
                                @Override
                                public boolean isValid() {
                                    return Game.getOpenInterfaceId() == Constants.STORE_ID;
                                }
                            }, 2500);
                            if (Game.getOpenInterfaceId() == Constants.STORE_ID) {
                                Interface shop = Loader.getClient().getInterfaceCache()[3900];
                                int[] itemIds;
                                if (shop != null && (itemIds = shop.getItems()) != null && itemIds.length > 0) {
                                    ArrayList<Item> items = new ArrayList<>();
                                    int[] ids = getItemIDs(shop);
                                    int[] stacks = getItemStacks(shop);
                                    for (int f = 0; f < ids.length; f++) {
                                        if (ids[f] > 0) {
                                            items.add(new Item(ids[f], stacks[f], f));
                                        }
                                    }
                                    for (Item item : items) {
                                        if (item.getId() == storeItem.getId() + 1) {
                                            Menu.sendAction(632, item.getId() - 1, item.getSlot(), 3900);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Time.sleep(new SleepCondition() {
                        @Override
                        public boolean isValid() {
                            return received;
                        }
                    }, 3500);
                }
            }
        }
        listen = false;
        return storeItems;
    }

    private int[] getItemIDs(Interface i) {
        int[] items;
        if ((items = i.getItems()) != null && items.length > 0) {
            return items;
        }
        return new int[0];
    }

    private int[] getItemStacks(Interface i) {
        int[] stacks;
        if ((stacks = i.getStackSizes()) != null && stacks.length > 0) {
            return stacks;
        }
        return new int[0];
    }

    @Override
    public void messageReceived(MessageEvent messageEvent) {
        if (listen && messageEvent.getType() == 0 && messageEvent.getMessage().contains("for sale for")) {
            currentItem.addPrice(messageEvent.getMessage().split("\\(")[1].replaceAll(",", "").replace(")", ""));
            received = true;
        }
    }
}
