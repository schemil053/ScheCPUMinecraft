package de.emilschlampp.schecpuminecraft.schemilapi.inventory.pager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


public class PagedPane implements InventoryHolder {

    private Inventory inventory;

    private SortedMap<Integer, Page> pages = new TreeMap<>();
    private int currentIndex;
    private int pageSize;

    protected Button controlBack;
    protected Button controlNext;

    public PagedPane(int pageSize, int rows, String title) {
        Objects.requireNonNull(title, "title can not be null!");
        if (rows > 6) {
            throw new IllegalArgumentException("Rows must be <= 6, got " + rows);
        }
        if (pageSize > 6) {
            throw new IllegalArgumentException("Page size must be <= 6, got" + pageSize);
        }

        this.pageSize = pageSize;
        inventory = Bukkit.createInventory(this, rows * 9, color(title));

        pages.put(0, new Page(pageSize));
    }

    public PagedPane(String title) {
        this(4, 6, title);
    }

    public void addButton(Button button) {
        for (Entry<Integer, Page> entry : pages.entrySet()) {
            if (entry.getValue().addButton(button)) {
                if (entry.getKey() == currentIndex) {
                    reRender();
                }
                return;
            }
        }
        Page page = new Page(pageSize);
        page.addButton(button);
        pages.put(pages.lastKey() + 1, page);

        reRender();
    }

    public void removeButton(Button button) {
        for (Iterator<Entry<Integer, Page>> iterator = pages.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<Integer, Page> entry = iterator.next();
            if (entry.getValue().removeButton(button)) {

                if (entry.getValue().isEmpty()) {

                    if (pages.size() > 1) {
                        iterator.remove();
                    }

                    if (currentIndex >= pages.size()) {
                        currentIndex--;
                    }
                }

                if (entry.getKey() >= currentIndex) {
                    reRender();
                }
                return;
            }
        }
    }

    public int getPageAmount() {
        return pages.size();
    }


    public int getCurrentPage() {
        return currentIndex + 1;
    }

    public void selectPage(int index) {
        if (index < 0 || index >= getPageAmount()) {
            throw new IllegalArgumentException(
                    "Index out of bounds s: " + index + " [" + 0 + " " + getPageAmount() + ")"
            );
        }
        if (index == currentIndex) {
            return;
        }

        currentIndex = index;
        reRender();
    }

    public void reRender() {
        inventory.clear();
        if(pages.get(currentIndex) != null) {
            pages.get(currentIndex).render(inventory);
        }

        controlBack = null;
        controlNext = null;
        createControls(inventory);
    }


    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getSlot() == inventory.getSize() - 8) {
            if (controlBack != null) {
                controlBack.onClick(event);
            }
            return;
        }

        else if (event.getSlot() == inventory.getSize() - 2) {
            if (controlNext != null) {
                controlNext.onClick(event);
            }
            return;
        }

        pages.get(currentIndex).handleClick(event);

    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }


    protected void createControls(Inventory inventory) {
        fillRow(
                inventory.getSize() / 9 - 2,
                getItemStack(Material.BLACK_STAINED_GLASS_PANE, 15, ""),
                inventory
        );

        if (getCurrentPage() > 1) {
            String name = String.format(
                    Locale.ROOT,
                    "&3&lSeite &a&l%d &7/ &c&l%d",
                    getCurrentPage() - 1, getPageAmount()
            );
            String lore = String.format(
                    Locale.ROOT,
                    "&7Bringt dich zur Seite &c%d",
                    getCurrentPage() - 1
            );
            ItemStack itemStack = getItemStack(Material.COAL_BLOCK, 0, name, lore);
            controlBack = new Button(itemStack, event -> selectPage(currentIndex - 1));
            inventory.setItem(inventory.getSize() - 8, itemStack);
        }

        if (getCurrentPage() < getPageAmount()) {
            String name = String.format(
                    Locale.ROOT,
                    "&3&lSeite &a&l%d &7/ &c&l%d",
                    getCurrentPage() + 1, getPageAmount()
            );
            String lore = String.format(
                    Locale.ROOT,
                    "&7Bringt dich zur Seite &c%d",
                    getCurrentPage() + 1
            );
            ItemStack itemStack = getItemStack(Material.IRON_BLOCK, 0, name, lore);
            controlNext = new Button(itemStack, event -> selectPage(getCurrentPage()));
            inventory.setItem(inventory.getSize() - 2, itemStack);
        }

        {
            String name = String.format(
                    Locale.ROOT,
                    "&3&lSeite &a&l%d &7/ &c&l%d",
                    getCurrentPage(), getPageAmount()
            );
            String lore = String.format(
                    Locale.ROOT,
                    "&7Seite &a%d &7/ &c%d",
                    getCurrentPage(), getPageAmount()
            );
            ItemStack itemStack = getItemStack(Material.BOOK, 0, name, lore);
            inventory.setItem(inventory.getSize() - 5, itemStack);
        }
    }

    private void fillRow(int rowIndex, ItemStack itemStack, Inventory inventory) {
        int yMod = rowIndex * 9;
        for (int i = 0; i < 9; i++) {
            int slot = yMod + i;
            inventory.setItem(slot, itemStack);
        }
    }

    protected ItemStack getItemStack(Material type, int durability, String name, String... lore) {
        ItemStack itemStack = new ItemStack(type, 1, (short) durability);

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (name != null) {
            itemMeta.setDisplayName(color(name));
        }
        if (lore != null && lore.length != 0) {
            itemMeta.setLore(Arrays.stream(lore).map(this::color).collect(Collectors.toList()));
        }
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }


    protected String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }


    public void open(Player player) {
        reRender();
        player.openInventory(getInventory());
    }


    private static class Page {
        private List<Button> buttons = new ArrayList<>();
        private int maxSize;

        Page(int maxSize) {
            this.maxSize = maxSize;
        }


        void handleClick(InventoryClickEvent event) {
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
                return;
            }
            if (event.getSlot() >= buttons.size()) {
                return;
            }
            Button button = buttons.get(event.getSlot());
            button.onClick(event);
        }


        boolean hasSpace() {
            return buttons.size() < maxSize * 9;
        }


        boolean addButton(Button button) {
            if (!hasSpace()) {
                return false;
            }
            buttons.add(button);

            return true;
        }


        boolean removeButton(Button button) {
            return buttons.remove(button);
        }

        void render(Inventory inventory) {
            for (int i = 0; i < buttons.size(); i++) {
                Button button = buttons.get(i);

                inventory.setItem(i, button.getItemStack());
            }
        }


        boolean isEmpty() {
            return buttons.isEmpty();
        }
    }

}