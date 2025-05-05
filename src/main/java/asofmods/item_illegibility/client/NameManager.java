package asofmods.item_illegibility.client;

import asofmods.item_illegibility.Item_illegibility;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.util.datafix.fixes.BlockEntitySignTextStrictJsonFix.GSON;

public class NameManager {

    private Map<String,String> names;

    public NameManager() {
        names = new HashMap();
        load();
    }

    private Path configPath() {
        return Path.of(FMLPaths.CONFIGDIR.get() + "/item_illegibility_item_names.json");
    }

    public void load() {
        if (!Files.exists(configPath())) {
            save();
            return;
        }

        try (var input = Files.newInputStream(configPath())) {
            names = GSON.fromJson(new InputStreamReader(input, StandardCharsets.UTF_8), Map.class);
        } catch (IOException e) {
            Item_illegibility.LOGGER.warn("Unable to load config file!");
        }
    }

    public void save() {
        try (var output = Files.newOutputStream(configPath()); var writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            GSON.toJson(names, writer);
        } catch (IOException e) {
            Item_illegibility.LOGGER.warn("Unable to save config file!");
        }
    }

    private String getKey(ItemStack itemStack) {
        return itemStack.getDescriptionId();
    }

    public void setName(ItemStack itemStack, String name) {
        if(itemStack.isEmpty()) Item_illegibility.LOGGER.error("nothing already has a name, that's the whole point!!");
        if(Objects.equals(name, "")) Item_illegibility.LOGGER.error("A name of length zero.. that's strictly absurd..");

        Item_illegibility.LOGGER.info(String.format("set name of %s to '%s'",itemStack.getDescriptionId(),name));
        names.put(getKey(itemStack),name);
        save();
    }

    public boolean hasName(ItemStack itemStack) {
        return names.containsKey(getKey(itemStack));
    }

    public String getName(ItemStack itemStack) {
        return names.get(getKey(itemStack));
    }

    public String getNameOrEmpty(ItemStack itemStack) {
        if(!hasName(itemStack)) return "";
        return getName(itemStack);
    }
}
