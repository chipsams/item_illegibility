package asofmods.item_illegibility.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static asofmods.item_illegibility.client.ClientMod.UNNAMED_STYLE;
import static asofmods.item_illegibility.client.ClientMod.nameManager;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Shadow public abstract String getDescriptionId();

    @Shadow public abstract ItemStack copy();

    @Shadow public abstract Item getItem();

    @Shadow public abstract boolean hasCustomHoverName();

    @Inject(at = @At("TAIL"), method = "getHoverName", cancellable = true)
    public void getHoverName(CallbackInfoReturnable<Component> cir) {
        if(this.hasCustomHoverName()) return;
        if(nameManager.hasName(this.copy())) {
            cir.setReturnValue(Component.literal(nameManager.getName(this.copy())));
        } else {
            cir.setReturnValue(Component.empty().append(cir.getReturnValue()).withStyle(UNNAMED_STYLE));
        }

    }
}
