package org.multibit.hd.ui.swing;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *         
 */
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec.DefaultAlignment;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.*;
import java.awt.*;

/**
 * Demonstrates how to build panels that honor or ignore the current
 * component orientation: left-to-right vs. right-to-left.<p>
 *
 * This example uses a utility class that may be moved to the extras or
 * to the Forms core in a future version. The tricky part is the abstract
 * definition of column specifications and cell constraints.<p>
 *
 * The example below utilizes the <code>OrientationUtils</code> to flip
 * column specification defaul alignments and to reverse the order of
 * column specifications. Cell constraints need to be adjusted too; this
 * example avoids the problem by using a builder that creates <em>all</em>
 * cell constraints.<p>
 *
 * You can find information about the latest additions regarding the
 * Forms support for different component orientations in the comments for
 * <a href="http://forms.dev.java.net/issues/show_bug.cgi?id=2">issue #2</a>.
 *
 * @author  Karsten Lentzsch
 * @version $Revision: 1.6 $
 *
 * @see     com.jgoodies.forms.builder.AbstractFormBuilder
 * @see     com.jgoodies.forms.builder.DefaultFormBuilder
 */
public class ComponentOrientationExample {

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
    } catch (Exception e) {
      // Likely PlasticXP is not in the class path; ignore.
    }
    JFrame frame = new JFrame();
    frame.setTitle("Forms Tutorial :: Component Orientation");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    JComponent panel = new ComponentOrientationExample().buildPanel();
    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);
  }


  public JComponent buildPanel() {
    FormLayout layout = new FormLayout("pref:grow");
    DefaultFormBuilder rowBuilder = new DefaultFormBuilder(layout);
    rowBuilder.setDefaultDialogBorder();

    rowBuilder.append(buildSample("Left to Right",       true));
    rowBuilder.append(buildSample("Right to Left",       false));
    rowBuilder.append(buildSample("Default Orientation",
      new PanelBuilder(layout).isLeftToRight()));

    return rowBuilder.getPanel();
  }

  /**
   * Creates and returns a sample panel that consists of a titled
   * separator and two component lines each with a 'leading' label.
   * Honors the specified component orientation.<p>
   *
   * The builder code avoids creating individual cell constraints;
   * all cell constraints used in the example below will be created
   * on-the-fly by the builder layer.<p>
   *
   * Note that cell constraints should be flipped and repositioned
   * if they are intended for being used with left-to-right and
   * right-to-left layouts.
   *
   * @return the sample panel
   */
  private Component buildSample(String title, boolean leftToRight) {
    String leftToRightSpecs = "right:pref, 4dlu, pref:grow, 3dlu, pref:grow";
    FormLayout layout = leftToRight
      ? new FormLayout(leftToRightSpecs)
      : new FormLayout(OrientationUtils.flipped(leftToRightSpecs),
      new RowSpec[] {});
    DefaultFormBuilder builder = new DefaultFormBuilder(layout);
    builder.setLeftToRight(leftToRight);
    builder.setDefaultDialogBorder();

    builder.appendSeparator(title);
    builder.append("Level");
    builder.append(new JTextField(10), 3);

    builder.append("Radar", new JTextField(10));
    builder.append(new JTextField(10));
    return builder.getPanel();
  }


  // Helper Code ************************************************************

  /**
   * Provides some convenience behavior for flipping side in column
   * specifications, arrays of column specifications and encoded
   * column specs.
   */
  private static class OrientationUtils {

    /**
     * Flips the default alignment of the given column specification
     * and returns a new column specification object with the flipped
     * alignment and the same size and growing behavior as the original.
     *
     * @param spec    the original column specification
     * @return the column specification with flipped default alignment
     */
    static ColumnSpec flipped(ColumnSpec spec) {
      DefaultAlignment alignment = spec.getDefaultAlignment();
      if (alignment == ColumnSpec.LEFT)
        alignment = ColumnSpec.RIGHT;
      else if (alignment == ColumnSpec.RIGHT)
        alignment = ColumnSpec.LEFT;
      return new ColumnSpec(alignment, spec.getSize(), spec.getResizeWeight());
    }

    /**
     * Returns an array of column specifications that is built from the
     * given array by flipping each column spec and reversing their order.
     *
     * @param original  the original array of column specifications
     * @return an array of flipped column specs in reversed order
     */
    static ColumnSpec[] flipped(ColumnSpec[] original) {
      int length = original.length;
      ColumnSpec[] flipped = new ColumnSpec[length];
      for (int i = 0; i < length; i++) {
        flipped[i] = flipped(original[length -1 - i]);
      }
      return flipped;
    }

    /**
     * Returns an array of column specifications that is built from the
     * given encoded column specifications by flipping each column spec
     * and reversing their order.
     *
     * @param encodedColumnSpecs  the original comma-separated encoded
     *     column specifications
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
     * @param cc   the original cell constraints object
     * @return the flipped cell constraints with flipped horizontal
     *     alignment, and flipped left and right insets - if any
     */
    static CellConstraints flipHorizontally(CellConstraints cc) {
      CellConstraints.Alignment flippedHAlign = cc.hAlign;
      if (flippedHAlign == CellConstraints.LEFT)
        flippedHAlign = CellConstraints.RIGHT;
      else if (flippedHAlign == CellConstraints.RIGHT)
        flippedHAlign = CellConstraints.LEFT;

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
     * @param cc   the original cell constraints object
     * @param columnCount the number of columns;
     *     used to swap the left and right cell bounds
     * @return the flipped cell constraints with flipped horizontal
     *     alignment, and flipped left and right insets - if any
     */
    static CellConstraints flipHorizontally(CellConstraints cc, int columnCount) {
      CellConstraints flipped = flipHorizontally(cc);
      flipped.gridX = columnCount + 1 - cc.gridX;
      return flipped;
    }
  }

}


