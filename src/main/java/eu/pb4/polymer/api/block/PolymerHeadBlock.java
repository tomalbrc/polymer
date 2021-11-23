package eu.pb4.polymer.api.block;

import eu.pb4.polymer.api.utils.PolymerUtils;
import eu.pb4.polymer.mixin.block.BlockEntityUpdateS2CPacketAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface PolymerHeadBlock extends PolymerBlock {
    /**
     * This method is used to determine texture/skin of client sided head
     * Player Skin can be generated by https://mineskin.org/
     *
     * @param state Server-side BlockState
     * @return Skin Value
     */
    String getPolymerSkinValue(BlockState state);

    default Block getPolymerBlock() {
        return Blocks.PLAYER_HEAD;
    }

    /**
     * Creates tag of Skull block entity
     *
     * @param state Server-side BlockState
     * @return NbtCompound representing client-side
     */
    default NbtCompound getPolymerHeadSkullOwner(BlockState state) {
        return PolymerUtils.createSkullOwner(((PolymerHeadBlock) state.getBlock()).getPolymerSkinValue(state));
    }

    /**
     * Creates client-side skull BlockEntity
     *
     * @param state Server-side BlockState
     * @param pos Block's position
     * @return A Packet
     */
    default Packet<?> getPolymerHeadPacket(BlockState state, BlockPos pos) {
        NbtCompound main = new NbtCompound();
        NbtCompound skullOwner = this.getPolymerHeadSkullOwner(state);
        main.putString("id", "minecraft:skull");
        main.put("SkullOwner", skullOwner);
        main.putInt("x", pos.getX());
        main.putInt("y", pos.getY());
        main.putInt("z", pos.getZ());
        return BlockEntityUpdateS2CPacketAccessor.createBlockEntityUpdateS2CPacket(pos, BlockEntityType.SKULL, main);
    }

    default void onPolymerBlockSend(ServerPlayerEntity player, BlockPos.Mutable pos, BlockState blockState) {
        player.networkHandler.sendPacket(this.getPolymerHeadPacket(blockState, pos.toImmutable()));
    }
}
