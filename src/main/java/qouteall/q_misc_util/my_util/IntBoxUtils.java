package qouteall.q_misc_util.my_util;

import net.minecraft.core.BlockPos;

public class IntBoxUtils {

    public static IntBox getBoxByPosAndSignedSize(
            BlockPos basePos,
            BlockPos signedSize
    ) {
        return new IntBox(
                basePos,
                new BlockPos(
                        getEndCoordWithSignedSize(basePos.getX(), signedSize.getX()),
                        getEndCoordWithSignedSize(basePos.getY(), signedSize.getY()),
                        getEndCoordWithSignedSize(basePos.getZ(), signedSize.getZ())
                )
        );
    }

    private static int getEndCoordWithSignedSize(int base, int signedSize) {
        if (signedSize > 0) {
            return base + signedSize - 1;
        }
        else if (signedSize < 0) {
            return base + signedSize + 1;
        }
        else {
            throw new IllegalArgumentException("Signed size cannot be zero");
        }
    }

    public static boolean isOnEdge(IntBox intBox, BlockPos pos) {
        boolean xOnEnd = pos.getX() == intBox.l.getX() || pos.getX() == intBox.h.getX();
        boolean yOnEnd = pos.getY() == intBox.l.getY() || pos.getY() == intBox.h.getY();
        boolean zOnEnd = pos.getZ() == intBox.l.getZ() || pos.getZ() == intBox.h.getZ();

        return (xOnEnd && yOnEnd) || (yOnEnd && zOnEnd) || (zOnEnd && xOnEnd);
    }
}
