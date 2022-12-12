package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TileJButton extends JButton
{
    private static TileJButton selectedButton = null;
    public static final Color LIGHT_COLOR = new Color(246,219,178);
    public static final Color DARK_COLOR = new Color(188,90,34);
    public static final Color SELECTED_COLOR = new Color(131,234,104);
    public static final Color LEGAL_LIGHT_COLOR = new Color( 231, 234, 95);
    public static final Color LEGAL_DARK_COLOR = new Color( 243,211,86);
    private boolean selected = false;

    private Color color, legalColor;
    private int coord;

    public TileJButton(int coord, int r, int c, String fileName)
    {
        this.coord = coord;
        if(r % 2 == c % 2)
        {
            color = DARK_COLOR;
            legalColor = LEGAL_DARK_COLOR;
        } else
        {
            color = LIGHT_COLOR;
            legalColor = LEGAL_LIGHT_COLOR;
        }

        setBackground(color);
        setBorder(null);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);
        setImage(fileName);
    }

    public int getCoord()
    {
        return coord;
    }

    public static TileJButton getSelectedButton()
    {
        return selectedButton;
    }

    @Override
    public boolean isSelected()
    {
        return selected;
    }

    public void setImage(String fileName)
    {
        if(fileName != null)
        {
            try
            {
                BufferedImage image = ImageIO.read(new File(fileName));
                StretchIcon icon = new StretchIcon(image);
                setIcon(icon);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            setIcon(null);
        }
    }

    public void setasLegalTile(boolean legal)
    {
        if(legal)   setBackground(legalColor);
        else        setBackground(color);
    }

    public void select()
    {
        selected = !selected;

        if(selected)
        {
            if( selectedButton != null )
            {
                selectedButton.selected = false;
                selectedButton.setBackground(selectedButton.color);
            }
            setBackground(SELECTED_COLOR);
            selectedButton = this;
        }
        else setBackground(color);
    }

    public void deselect()
    {
        selectedButton = null;
        setBackground(color);
        selected = false;
    }
}
