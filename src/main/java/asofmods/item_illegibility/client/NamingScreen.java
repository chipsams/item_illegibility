package asofmods.item_illegibility.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.Objects;

import static asofmods.item_illegibility.client.ClientMod.MODID;
import static asofmods.item_illegibility.client.ClientMod.nameManager;

public class NamingScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();


    static int BACKGROUND_WIDTH = 104;
    static int BACKGROUND_HEIGHT = 66;

    static int TEXTBOX_X = 7;
    static int TEXTBOX_Y = 43;
    static int TEXTBOX_WIDTH = 90;
    static int TEXTBOX_HEIGHT = 16;
    private final String initialItemName;

    private ItemStack itemStack;
    private String itemName = "";

    public NamingScreen(ItemStack itemStack) {
        super(Component.translatable("item_illegibility.gui.naming_screen.title"));

        this.itemStack = itemStack;
        this.initialItemName = nameManager.getNameOrEmpty(itemStack);
    }

    private EditBox editBox;

    @Override
    protected void init() {

        super.init();

        minecraft.player.playSound(new SoundEvent(new ResourceLocation("minecraft","ui.button.click")));

        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        int topLeftX = width/2-BACKGROUND_WIDTH/2;
        int topLeftY = height/2-BACKGROUND_HEIGHT/2;

        String s = this.editBox != null ? this.editBox.getValue() : this.initialItemName;

        // Add widgets and precomputed values
        editBox = this.addRenderableWidget(new EditBox(
                Minecraft.getInstance().font,
                topLeftX+TEXTBOX_X+3, topLeftY+TEXTBOX_Y+3, TEXTBOX_WIDTH-3, TEXTBOX_HEIGHT-3,
                Component.literal("?")
        ));
        editBox.setMaxLength(30);
        editBox.setVisible(true);
        editBox.setFocus(true);
        editBox.setBordered(false);
        editBox.setValue(s);
        editBox.setTextColor(0xffffffff);
    }

    private boolean canSubmit(){
        // if the name wouldn't change upon submit
        if(Objects.equals(nameManager.getNameOrEmpty(itemStack),editBox.getValue())) return false;
        // if there is no name input at all
        if(Objects.equals(editBox.getValue(), "")) return false;

        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.editBox.tick();
    }

    @Override
    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {

        if(p_96552_ == InputConstants.KEY_RETURN && canSubmit()){
            nameManager.setName(itemStack,editBox.getValue());
            assert minecraft.player != null;
            minecraft.player.playSound(new SoundEvent(new ResourceLocation("minecraft","ui.cartography_table.take_result")));
            return true;
        }

        if(editBox.isFocused()) {
            if (editBox.keyPressed(p_96552_, p_96553_, p_96554_)) return true;
        }

        return super.keyPressed(p_96552_, p_96553_,  p_96554_);
    }

    @Override
    public boolean charTyped(char p_94683_, int modifiers) {

        if(editBox.isFocused()){
            if(editBox.charTyped(p_94683_,modifiers)) return true;
        }

        return super.charTyped(p_94683_, modifiers);
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float p_96565_) {
        this.renderBackground(pose);


        RenderSystem.setShaderTexture(0, new ResourceLocation(MODID,"textures/gui/item_naming.png"));
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        int topLeftX = width/2-BACKGROUND_WIDTH/2;
        int topLeftY = height/2-BACKGROUND_HEIGHT/2;

        GuiComponent.blit(
                pose,topLeftX,topLeftY,
                0,0,0,
                BACKGROUND_WIDTH,BACKGROUND_HEIGHT,
                128,128
        );
        GuiComponent.blit(
                pose,topLeftX+7,topLeftY+43,
                0,0,128 - TEXTBOX_HEIGHT * (canSubmit() ? 1 : 2),
                TEXTBOX_WIDTH,TEXTBOX_HEIGHT,
                128,128
        );

        this.editBox.render(pose,mouseX,mouseY,p_96565_);

        this.itemRenderer.renderGuiItem(this.itemStack, topLeftX+17,topLeftY+17);

        super.render(pose,mouseX,mouseY,p_96565_);
    }
}
