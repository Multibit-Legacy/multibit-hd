package org.multibit.hd.ui.swing.views;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpec;

import java.awt.*;

/**
 * <p>Provides some convenience behavior for flipping side in column
 * specifications, arrays of column specifications and encoded
 * column specs.</p>
 *
 * @since 0.0.1
 *         
 */
public class OrientationUtils {

  /**
   * Flips the default alignment of the given column specification
   * and returns a new column specification object with the flipped
   * alignment and the same size and growing behavior as the original.
   *
   * @param spec the original column specification
   *
   * @return the column specification with flipped default alignment
   */
  static ColumnSpec flipped(ColumnSpec spec) {

    FormSpec.DefaultAlignment alignment = spec.getDefaultAlignment();

    if (alignment == ColumnSpec.LEFT) {
      alignment = ColumnSpec.RIGHT;
    } else if (alignment == ColumnSpec.RIGHT) {
      alignment = ColumnSpec.LEFT;
    }

    return new ColumnSpec(alignment, spec.getSize(), spec.getResizeWeight());
  }

  /**
   * Returns an array of column specifications that is built from the
   * given array by flipping each column spec and reversing their order.
   *
   * @param original the original array of column specifications
   *
   * @return an array of flipped column specs in reversed order
   */
  static ColumnSpec[] flipped(ColumnSpec[] original) {

    int length = original.length;

    ColumnSpec[] flipped = new ColumnSpec[length];
    for (int i = 0; i < length; i++) {
      flipped[i] = flipped(original[length - 1 - i]);
    }

    return flipped;
  }

  /**
   * Returns an array of column specifications that is built from the
   * given encoded column specifications by flipping each column spec
   * and reversing their order.
   *
   * @param encodedColumnSpecs the original comma-separated encoded
   *                           column specifications
   *
   * @return an array of flipped column specs in reversed order
   */
  static ColumnSpec[] flipped(String encodedColumnSpecs) {
    return flipped(ColumnSpec.decodeSpecs(encodedColumnSpecs));
  }

  /**
   * Creates and returns a horizontally flipped clone of the
   * given cell constraints object. Flips the horizontal alignment
   * and the left and right insets.
   *
   * @param cc the original cell constraints object
   *
   * @return the flipped cell constraints with flipped horizontal
   *         alignment, and flipped left and right insets - if any
   */
  static CellConstraints flipHorizontally(CellConstraints cc) {

    CellConstraints.Alignment flippedHAlign = cc.hAlign;

    if (flippedHAlign == CellConstraints.LEFT) {
      flippedHAlign = CellConstraints.RIGHT;
    } else if (flippedHAlign == CellConstraints.RIGHT) {
      flippedHAlign = CellConstraints.LEFT;
    }

    CellConstraints flipped = new CellConstraints(
      cc.gridX,
      cc.gridY,
      cc.gridWidth,
      cc.gridHeight,
      flippedHAlign,
      cc.vAlign);

    if (cc.insets != null) {
      flipped.insets = new Insets(
        cc.insets.top,
        cc.insets.right,
        cc.insets.bottom,
        cc.insets.left);
    }

    return flipped;
  }

  /**
   * Creates and returns a horizontally flipped clone of the
   * given cell constraints object with the grid position adjusted
   * to the given column count. Flips the horizontal alignment
   * and the left and right insets. And swaps the left and right
   * cell positions according to the specified column count.
   *
   * @param cc          the original cell constraints object
   * @param columnCount the number of columns; used when swapping the left and right cell bounds
   *
   * @return the flipped cell constraints with flipped horizontal
   *         alignment, and flipped left and right insets - if any
   */
  static CellConstraints flipHorizontally(CellConstraints cc, int columnCount) {

    CellConstraints flipped = flipHorizontally(cc);
    flipped.gridX = columnCount + 1 - cc.gridX;
    return flipped;

  }
}
