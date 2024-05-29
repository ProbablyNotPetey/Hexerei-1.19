package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.connected.CTDyable;
import net.joefoxe.hexerei.block.connected.FabricBlock;
import net.joefoxe.hexerei.block.connected.Waxed;
import net.joefoxe.hexerei.block.connected.WaxedLayeredBlockDyed;
import net.joefoxe.hexerei.item.custom.SatchelItem;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;

public class ConnectingCarpetDyed extends CarpetBlock implements DyeableLeatherItem, Waxed, CTDyable {

    public static BooleanProperty WEST = BooleanProperty.create("west"),
                                  EAST = BooleanProperty.create("east");
    public static final EnumProperty<North> NORTH = EnumProperty.create("north", North.class);
    public static final EnumProperty<South> SOUTH = EnumProperty.create("south", South.class);

    public DyeColor dyeColor;

    public ConnectingCarpetDyed(Properties pProperties, DyeColor dyeColor){
        super(pProperties.noOcclusion());
        registerDefaultState(super.defaultBlockState()
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(NORTH, North.NONE)
                .setValue(SOUTH, South.NONE));

        this.dyeColor = dyeColor;
    }


    @Override
    public DyeColor getDyeColor() {
        return dyeColor;
    }



    public BlockState rotate(BlockState pState, Rotation pRot) {
        boolean east = pState.getValue(EAST);
        boolean west = pState.getValue(WEST);
        North northState = pState.getValue(NORTH);
        South southState = pState.getValue(SOUTH);
        boolean north = northState == North.ALL || northState == North.JUST_NORTH || northState == North.NORTH_AND_NORTH_EAST || northState == North.NORTH_AND_NORTH_WEST;
        boolean north_east = northState == North.ALL || northState == North.JUST_NORTH_EAST || northState == North.NORTH_AND_NORTH_EAST || northState == North.NORTH_EAST_AND_NORTH_WEST;
        boolean north_west = northState == North.ALL || northState == North.JUST_NORTH_WEST || northState == North.NORTH_AND_NORTH_WEST || northState == North.NORTH_EAST_AND_NORTH_WEST;
        boolean south = southState == South.ALL || southState == South.JUST_SOUTH || southState == South.SOUTH_AND_SOUTH_EAST || southState == South.SOUTH_AND_SOUTH_WEST;
        boolean south_east = southState == South.ALL || southState == South.JUST_SOUTH_EAST || southState == South.SOUTH_AND_SOUTH_EAST || southState == South.SOUTH_EAST_AND_SOUTH_WEST;
        boolean south_west = southState == South.ALL || southState == South.JUST_SOUTH_WEST || southState == South.SOUTH_AND_SOUTH_WEST || southState == South.SOUTH_EAST_AND_SOUTH_WEST;

        switch (pRot){
            case NONE -> {
                return pState;
            }
            case CLOCKWISE_90 -> {
                North northTemp = North.NONE;
                South southTemp = South.NONE;
                if(south_east && east && north_east)
                    southTemp = South.ALL;
                else if (!south_east && east && north_east)
                    southTemp = South.SOUTH_AND_SOUTH_EAST;
                else if (south_east && east)
                    southTemp = South.SOUTH_AND_SOUTH_WEST;
                else if (south_east && north_east)
                    southTemp = South.SOUTH_EAST_AND_SOUTH_WEST;
                else if (!south_east && east)
                    southTemp = South.JUST_SOUTH;
                else if (!south_east && north_east)
                    southTemp = South.JUST_SOUTH_EAST;
                else if (south_east)
                    southTemp = South.JUST_SOUTH_WEST;

                if(south_west && west && north_west)
                    northTemp = North.ALL;
                else if (!south_west && west && north_west)
                    northTemp = North.NORTH_AND_NORTH_EAST;
                else if (south_west && west)
                    northTemp = North.NORTH_AND_NORTH_WEST;
                else if (south_west && north_west)
                    northTemp = North.NORTH_EAST_AND_NORTH_WEST;
                else if (!south_west && west)
                    northTemp = North.JUST_NORTH;
                else if (!south_west && north_west)
                    northTemp = North.JUST_NORTH_EAST;
                else if (south_west)
                    northTemp = North.JUST_NORTH_WEST;

                return pState.setValue(EAST, north).setValue(WEST, south).setValue(NORTH, northTemp).setValue(SOUTH, southTemp);
            }
            case CLOCKWISE_180 -> {
                North northTemp = North.NONE;
                South southTemp = South.NONE;
                if (north && north_east && north_west)
                    southTemp = South.ALL;
                else if (north && north_west)
                    southTemp = South.SOUTH_AND_SOUTH_EAST;
                else if (north && north_east)
                    southTemp = South.SOUTH_AND_SOUTH_WEST;
                else if (north_west && north_east)
                    southTemp = South.SOUTH_EAST_AND_SOUTH_WEST;
                else if (!north_west && !north_east && north)
                    southTemp = South.JUST_SOUTH;
                else if (north_west)
                    southTemp = South.JUST_SOUTH_EAST;
                else if (north_east)
                    southTemp = South.JUST_SOUTH_WEST;

                if (south && south_east && south_west)
                    northTemp = North.ALL;
                else if (south && south_west)
                    northTemp = North.NORTH_AND_NORTH_EAST;
                else if (south && south_east)
                    northTemp = North.NORTH_AND_NORTH_WEST;
                else if (south_west && south_east)
                    northTemp = North.NORTH_EAST_AND_NORTH_WEST;
                else if (!south_west && !south_east && south)
                    northTemp = North.JUST_NORTH;
                else if (south_west)
                    northTemp = North.JUST_NORTH_EAST;
                else if (south_east)
                    northTemp = North.JUST_NORTH_WEST;

                return pState.setValue(EAST, west).setValue(WEST, east).setValue(NORTH, northTemp).setValue(SOUTH, southTemp);

            }
            case COUNTERCLOCKWISE_90 -> {
                North northTemp = North.NONE;
                South southTemp = South.NONE;
                if(north_west && west && south_west)
                    southTemp = South.ALL;
                else if (!north_west && west && south_west)
                    southTemp = South.SOUTH_AND_SOUTH_EAST;
                else if (north_west && west)
                    southTemp = South.SOUTH_AND_SOUTH_WEST;
                else if (north_west && south_west)
                    southTemp = South.SOUTH_EAST_AND_SOUTH_WEST;
                else if (!north_west && west)
                    southTemp = South.JUST_SOUTH;
                else if (!north_west && south_west)
                    southTemp = South.JUST_SOUTH_EAST;
                else if (north_west)
                    southTemp = South.JUST_SOUTH_WEST;

                if(north_east && east && south_east)
                    northTemp = North.ALL;
                else if (!north_east && east && south_east)
                    northTemp = North.NORTH_AND_NORTH_EAST;
                else if (north_east && east)
                    northTemp = North.NORTH_AND_NORTH_WEST;
                else if (north_east && south_east)
                    northTemp = North.NORTH_EAST_AND_NORTH_WEST;
                else if (!north_east && east)
                    northTemp = North.JUST_NORTH;
                else if (!north_east && south_east)
                    northTemp = North.JUST_NORTH_EAST;
                else if (north_east)
                    northTemp = North.JUST_NORTH_WEST;

                return pState.setValue(EAST, south).setValue(WEST, north).setValue(NORTH, northTemp).setValue(SOUTH, southTemp);
            }
        }
        return pState;
//        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos blockpos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if(player.getItemInHand(pHand).getItem() instanceof DyeItem dyeItem) {
            DyeColor dyecolor = dyeItem.getDyeColor();
            if(this.dyeColor == dyecolor)
                return InteractionResult.FAIL;

            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, player.getItemInHand(pHand));
            }
            BlockState newBlockstate = getBlockByColor(dyecolor).defaultBlockState();

            if(!player.isCreative())
                player.getItemInHand(pHand).shrink(1);
            if(!player.isCreative() && pState.getBlock() == ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get())
                Block.popResource(pLevel, blockpos, new ItemStack(Items.GOLD_NUGGET));
            pLevel.setBlockAndUpdate(blockpos, newBlockstate);
            pLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
            pLevel.levelEvent(player, 3003, blockpos, 0);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);

        }
        else if(player.getItemInHand(pHand).getItem() == Items.GOLD_NUGGET) {
            if(pState.getBlock() == ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get())
                return InteractionResult.FAIL;

            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, player.getItemInHand(pHand));
            }
            BlockState newBlockstate = ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get().defaultBlockState();
            if(!player.isCreative())
                player.getItemInHand(pHand).shrink(1);

            pLevel.setBlockAndUpdate(blockpos, newBlockstate);
            pLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
            pLevel.levelEvent(player, 3004, blockpos, 0);
            pLevel.playSound(player, blockpos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);

        }

        return super.use(pState, pLevel, blockpos, player, pHand, pHit);
    }


    public static Block getBlockByColor(@Nullable DyeColor pColor) {
        if (pColor == null) {
            return Blocks.SHULKER_BOX;
        } else {
            return switch (pColor) {
                case WHITE -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_WHITE.get();
                case ORANGE -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_ORANGE.get();
                case MAGENTA -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_MAGENTA.get();
                case LIGHT_BLUE -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_BLUE.get();
                case YELLOW -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_YELLOW.get();
                case LIME -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIME.get();
                case PINK -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_PINK.get();
                case GRAY -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_GRAY.get();
                case LIGHT_GRAY -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_LIGHT_GRAY.get();
                case CYAN -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_CYAN.get();
                case PURPLE -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_PURPLE.get();
                case BLUE -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLUE.get();
                case BROWN -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_BROWN.get();
                case GREEN -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_GREEN.get();
                case RED -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_RED.get();
                case BLACK -> ModBlocks.INFUSED_FABRIC_CARPET_DYED_BLACK.get();
            };
        }
    }



    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        return getUnWaxed(state, context, toolAction);
    }

    public static int getColorValue(ItemStack stack) {
        if(Block.byItem(stack.getItem()) instanceof ConnectingCarpetDyed connectingCarpetDyed)
            return HexereiUtil.getColorValue(connectingCarpetDyed.dyeColor);
        else if(Block.byItem(stack.getItem()) instanceof WaxedLayeredBlockDyed waxedLayeredBlockDyed)
            return HexereiUtil.getColorValue(waxedLayeredBlockDyed.dyeColor);
        else if(Block.byItem(stack.getItem()) instanceof FabricBlock fabricBlock)
            return HexereiUtil.getColorValue(fabricBlock.dyeColor);
        return SatchelItem.getColorStatic(stack);
    }


    public static int getColorValue(BlockState state, BlockPos pos, BlockGetter level) {
        if (state.getBlock() instanceof CTDyable ctDyable)
            return HexereiUtil.getColorValue(ctDyable.getDyeColor());
        else if(state.getBlock() instanceof ConnectingCarpetStairs stairs && stairs.parentBlock instanceof ConnectingCarpetDyed connectingCarpetDyed)
            return HexereiUtil.getColorValue(connectingCarpetDyed.dyeColor);
        else if(state.getBlock() instanceof ConnectingCarpetSlab slab && slab.parentBlock instanceof ConnectingCarpetDyed connectingCarpetDyed)
            return HexereiUtil.getColorValue(connectingCarpetDyed.dyeColor);
        else if(state.getBlock() instanceof FabricBlock)
            return HexereiUtil.getColorValue(((FabricBlock)state.getBlock()).dyeColor);
        return HexereiUtil.getColorValue(DyeColor.BLACK);
    }


    protected BlockState updateCorners(BlockGetter world, BlockPos pos, BlockState state) {
        BlockState bs_north = world.getBlockState(pos.north());
        BlockState bs_north_east = world.getBlockState(pos.north().east());
        BlockState bs_north_west = world.getBlockState(pos.north().west());
        BlockState bs_east = world.getBlockState(pos.east());
        BlockState bs_south = world.getBlockState(pos.south());
        BlockState bs_south_east = world.getBlockState(pos.south().east());
        BlockState bs_south_west = world.getBlockState(pos.south().west());
        BlockState bs_west = world.getBlockState(pos.west());
        North north = North.NONE;
        South south = South.NONE;

        if(bs_north.getBlock() == this){
            north = North.JUST_NORTH;
            if(bs_north_west.getBlock() == this && bs_north_east.getBlock() != this){
                north = North.NORTH_AND_NORTH_WEST;
            }
            if(bs_north_west.getBlock() != this && bs_north_east.getBlock() == this){
                north = North.NORTH_AND_NORTH_EAST;
            }
            if(bs_north_west.getBlock() == this && bs_north_east.getBlock() == this){
                north = North.ALL;
            }
        }else{
            if(bs_north_west.getBlock() == this && bs_north_east.getBlock() != this){
                north = North.JUST_NORTH_WEST;
            }
            if(bs_north_west.getBlock() != this && bs_north_east.getBlock() == this){
                north = North.JUST_NORTH_EAST;
            }
        }
        if(bs_south.getBlock() == this){
            south = South.JUST_SOUTH;
            if(bs_south_west.getBlock() == this && bs_south_east.getBlock() != this){
                south = South.SOUTH_AND_SOUTH_WEST;
            }
            if(bs_south_west.getBlock() != this && bs_south_east.getBlock() == this){
                south = South.SOUTH_AND_SOUTH_EAST;
            }
            if(bs_south_west.getBlock() == this && bs_south_east.getBlock() == this){
                south = South.ALL;
            }
        }else{
            if(bs_south_west.getBlock() == this && bs_south_east.getBlock() != this){
                south = South.JUST_SOUTH_WEST;
            }
            if(bs_south_west.getBlock() != this && bs_south_east.getBlock() == this){
                south = South.JUST_SOUTH_EAST;
            }
        }


        boolean east = bs_east.getBlock() == this,
                west = bs_west.getBlock() == this;
        return state
                .setValue(NORTH, north).setValue(EAST, east)
                .setValue(SOUTH, south).setValue(WEST, west);
    }
    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return updateCorners(iblockreader, blockpos, super.getStateForPlacement(context));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WEST, EAST, NORTH, SOUTH);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {

        return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : updateCorners(world, pos, state);
    }
    public enum North implements StringRepresentable {
        JUST_NORTH,
        NORTH_AND_NORTH_WEST,
        NORTH_AND_NORTH_EAST,
        JUST_NORTH_WEST,
        JUST_NORTH_EAST,
        NORTH_EAST_AND_NORTH_WEST,
        ALL,
        NONE;

        public String toString() {
            return this.getSerializedName();
        }

        public String getSerializedName() {
            return switch (this){
                case JUST_NORTH -> "north";
                case NORTH_AND_NORTH_WEST -> "north_and_north_west";
                case NORTH_AND_NORTH_EAST -> "north_and_north_east";
                case JUST_NORTH_WEST -> "north_west";
                case JUST_NORTH_EAST -> "north_east";
                case NORTH_EAST_AND_NORTH_WEST -> "north_east_and_north_west";
                case ALL -> "all";
                case NONE -> "none";
            };
        }
    }
    public enum South implements StringRepresentable {
        JUST_SOUTH,
        SOUTH_AND_SOUTH_WEST,
        SOUTH_AND_SOUTH_EAST,
        JUST_SOUTH_WEST,
        JUST_SOUTH_EAST,
        SOUTH_EAST_AND_SOUTH_WEST,
        ALL,
        NONE;

        public String toString() {
            return this.getSerializedName();
        }

        public String getSerializedName() {
            return switch (this){
                case JUST_SOUTH -> "south";
                case SOUTH_AND_SOUTH_WEST -> "south_and_south_west";
                case SOUTH_AND_SOUTH_EAST -> "south_and_south_east";
                case JUST_SOUTH_WEST -> "south_west";
                case JUST_SOUTH_EAST -> "south_east";
                case SOUTH_EAST_AND_SOUTH_WEST -> "south_east_and_south_west";
                case ALL -> "all";
                case NONE -> "none";
            };
        }
    }
}
