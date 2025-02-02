package qouteall.mini_scaled.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.mixin.item.group.ItemGroupMixin;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import qouteall.mini_scaled.ScaleBoxGeneration;
import qouteall.mini_scaled.ScaleBoxManipulation;
import qouteall.mini_scaled.ScaleBoxRecord;

public class ScaleBoxEntranceItem extends Item {
    
    public static final ScaleBoxEntranceItem instance = new ScaleBoxEntranceItem(new FabricItemSettings());
    
    public static void init() {
        Registry.register(
            Registry.ITEM,
            new ResourceLocation("mini_scaled:scale_box_item"),
            instance
        );
    }
    
    public static class ItemInfo {
        public int scale;
        public DyeColor color;
        @Nullable
        public UUID ownerId;
        @Nullable
        public String ownerNameCache;
        
        public ItemInfo(int scale, DyeColor color) {
            this.scale = scale;
            this.color = color;
        }
        
        public ItemInfo(
            int size, DyeColor color, @NotNull UUID ownerId, @NotNull String ownerNameCache
        ) {
            this.scale = size;
            this.color = color;
            this.ownerId = ownerId;
            this.ownerNameCache = ownerNameCache;
        }
        
        public ItemInfo(CompoundTag tag) {
            scale = tag.getInt("size");
            color = DyeColor.byName(tag.getString("color"), DyeColor.BLACK);
            if (tag.contains("ownerId")) {
                ownerId = tag.getUUID("ownerId");
                ownerNameCache = tag.getString("ownerNameCache");
            }
        }
        
        public void writeToTag(CompoundTag compoundTag) {
            compoundTag.putInt("size", scale);
            compoundTag.putString("color", color.getName());
            if (ownerId != null) {
                compoundTag.putUUID("ownerId", ownerId);
                compoundTag.putString("ownerNameCache", ownerNameCache);
            }
        }
    }
    
    public ScaleBoxEntranceItem(Properties settings) {
        super(settings);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
    
        return ScaleBoxManipulation.onRightClickUsingEntrance(context);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        ItemInfo itemInfo = new ItemInfo(stack.getOrCreateTag());
        tooltip.add(Component.translatable("mini_scaled.color")
            .append(getColorText(itemInfo.color).withStyle(ChatFormatting.GOLD))
        );
        tooltip.add(Component.translatable("mini_scaled.scale")
            .append(Component.literal(Integer.toString(itemInfo.scale)).withStyle(ChatFormatting.AQUA))
        );
        if (itemInfo.ownerNameCache != null) {
            tooltip.add(Component.translatable("mini_scaled.owner")
                .append(Component.literal(itemInfo.ownerNameCache).withStyle(ChatFormatting.YELLOW))
            );
        }

        // the entrance size is not shown because the size may change, and it's hard to update
//        if (itemInfo.entranceSizeCache != null) {
//            String sizeStr = String.format("%d x %d x %d",
//                itemInfo.entranceSizeCache.getX(), itemInfo.entranceSizeCache.getY(), itemInfo.entranceSizeCache.getZ()
//            );
//
//            tooltip.add(new TranslatableText("mini_scaled.entrance_size")
//                .append(new LiteralText(sizeStr))
//            );
//        }
    }
    
    public static NonNullList<ItemStack> getRelatedItems() {
        NonNullList<ItemStack> list = NonNullList.create();
        for (int scale : ScaleBoxGeneration.supportedScales) {
            for (DyeColor dyeColor : DyeColor.values()) {
                ItemStack itemStack = new ItemStack(instance);

                ItemInfo itemInfo = new ItemInfo(scale, dyeColor);
                itemInfo.writeToTag(itemStack.getOrCreateTag());

                list.add(itemStack);

            }
        }
        return list;
    }

    
    private static final Component spaceText = Component.literal(" ");
    
    @Override
    public Component getName(ItemStack stack) {
        ItemInfo itemInfo = new ItemInfo(stack.getOrCreateTag());
        DyeColor color = itemInfo.color;
        MutableComponent result = Component.translatable("item.mini_scaled.scale_box_item")
            .append(spaceText)
            .append(Component.literal(Integer.toString(itemInfo.scale)));
        if (itemInfo.ownerNameCache != null) {
            result = result.append(spaceText)
                .append(Component.translatable("mini_scaled.owner"))
                .append(Component.literal(itemInfo.ownerNameCache));
        }
        return result;
    }
    
    public static MutableComponent getColorText(DyeColor color) {
        return Component.translatable("color.minecraft." + color.getName());
    }
    
    @Nullable
    public static ItemStack boxIdToItem(int boxId) {
        ScaleBoxRecord.Entry entry = ScaleBoxRecord.get().getEntryById(boxId);
        if (entry == null) {
            System.err.println("invalid boxId for item " + boxId);
            return null;
        }
        
        ItemStack itemStack = new ItemStack(ScaleBoxEntranceItem.instance);
        new ScaleBoxEntranceItem.ItemInfo(
            entry.scale, entry.color, entry.ownerId, entry.ownerNameCache
        ).writeToTag(itemStack.getOrCreateTag());
        
        return itemStack;
    }
    
    public static int getRenderingColor(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null) {
            return 0;
        }
        // not using ItemInfo to improve performance
        String colorText = nbt.getString("color");
        DyeColor dyeColor = DyeColor.byName(colorText, DyeColor.BLACK);
        return dyeColor.getMaterialColor().col;
    }
}
