package asofmods.item_illegibility.client;

import asofmods.item_illegibility.Item_illegibility;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(Item_illegibility.MODID)
public class ClientMod {

    static Lazy<KeyMapping> NAME_ITEM_KEYMAPPING = Lazy.of(()->new KeyMapping(
            "key.itemillegiblity.name_item",
            KeyConflictContext.UNIVERSAL,
            KeyModifier.NONE, // Default mapping requires shift to be held down
            InputConstants.Type.KEYSYM, // Default mapping is on the keyboard
            GLFW.GLFW_KEY_G, // Default key is G
            "key.categories.misc"
    ));

    public ClientMod() {

        var context = FMLJavaModLoadingContext.get();
        IEventBus eventBus = context.getModEventBus();

        Item_illegibility.LOGGER.info("TRYING TO REGISTER LISTENER");
        eventBus.addListener(this::onRegisterBindings);

        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltipEvent);
    }

    // Event is on the mod event bus only on the physical client
    public void onRegisterBindings(RegisterKeyMappingsEvent event) {
        Item_illegibility.LOGGER.info("BINDING REGISTRY");
        event.register(NAME_ITEM_KEYMAPPING.get());
    }

    enum TooltipQueuedAction {
        NAME_THING
    }

    static List<TooltipQueuedAction> queuedActions = new ArrayList<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
            queuedActions.clear();
            while (NAME_ITEM_KEYMAPPING.get().consumeClick()) {
                Item_illegibility.LOGGER.info("NAMETHING CLICKED!!");
                if(Minecraft.getInstance().screen == null){
                    ItemStack heldItem = Minecraft.getInstance().player.getMainHandItem();
                    if(!heldItem.isEmpty()) Minecraft.getInstance().setScreen(new NamingScreen(heldItem));
                }else{
                    queuedActions.add(TooltipQueuedAction.NAME_THING);
                }
            }
        }
    }

    public static Map<String,String> item_names = new HashMap();

    @SubscribeEvent
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        // LOGGER.info("HELLO FROM TOOLTIP EVENT");
        //createDescriptionsFromItemStack(event.getItemStack(), event.getToolTip());
        for (TooltipQueuedAction action : queuedActions ) {
            Item_illegibility.LOGGER.info(action.name());
            switch (action) {
                case NAME_THING -> {
                    Item_illegibility.LOGGER.info(String.format("WE GOT EM.. %s", event.getItemStack()));
                    Minecraft.getInstance().setScreen(new NamingScreen(event.getItemStack()));
                }
                default -> { }
            }
        }
        queuedActions.clear();
    }

    public static Style UNNAMED_STYLE = Style.EMPTY.withObfuscated(true).withColor(ChatFormatting.DARK_RED);

    private void createDescriptionsFromItemStack(ItemStack itemStack, List<Component> toolTip) {
        if(itemStack.hasCustomHoverName()){
            Component firstLine = toolTip.get(0);
            toolTip.clear();
            toolTip.add(firstLine);
        }else if(Item_illegibility.nameManager.hasName(itemStack)){
            String text = Item_illegibility.nameManager.getName(itemStack);
            MutableComponent component = Component.literal(text);
            component.withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
            toolTip.clear();
            toolTip.add(component);
        } else {
            String text = "NOT NAMED";
            MutableComponent component = Component.literal(text);
            component.withStyle(UNNAMED_STYLE);
            toolTip.clear();
            toolTip.add(component);
        }
    }

}
