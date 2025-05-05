package asofmods.item_illegibility.mixin;

import asofmods.item_illegibility.Item_illegibility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static asofmods.item_illegibility.client.ClientMod.UNNAMED_STYLE;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Shadow public abstract String getDescriptionId();

    @Shadow public abstract ItemStack copy();

    @Shadow public abstract Item getItem();

    @Shadow public abstract boolean hasCustomHoverName();

    @Inject(at = @At("TAIL"), method = "getHoverName", cancellable = true)
    public void getHoverName(CallbackInfoReturnable<Component> cir) {
        if(this.hasCustomHoverName()) return;
        if(Item_illegibility.nameManager.hasName(this.copy())) {
            cir.setReturnValue(Component.literal(Item_illegibility.nameManager.getName(this.copy())));
        } else {
            cir.setReturnValue(Component.empty().append(cir.getReturnValue()).withStyle(UNNAMED_STYLE));
        }

    }
}
