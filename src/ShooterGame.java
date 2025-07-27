import javax.swing.JFrame;
import java.awt.Image; // Image class එක import කරනවා
import javax.swing.ImageIcon; // ImageIcon class එක import කරනවා

// GamePanel class එක import කරනවා (ඔබට package එකක් තියෙනවා නම් ඒකට අනුව වෙනස් කරන්න)
// import com.example.game.GamePanel;

public class ShooterGame {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Simple Shooter Game");

        // Icon එක load කරන කොටස
        try {
            // "game_icon.png" කියන එක ඔබේ icon file එකේ නම විය යුතුයි.
            // images/game_icon.png කියන්නේ src/images/game_icon.png වෙත යන path එකයි.
            // getClass().getClassLoader().getResource() මගින් JAR file එකක් තුල වුවද image එක load කරගත හැක.
            Image icon = new ImageIcon(ShooterGame.class.getClassLoader().getResource("rocket.png")).getImage();
            frame.setIconImage(icon); // JFrame එකට icon එක set කරනවා
        } catch (Exception e) {
            System.err.println("Icon image loading failed: " + e.getMessage());
            // icon එක load කරන්න බැරි වුනොත් error එක console එකේ පෙන්වනවා
        }

        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        frame.setVisible(true);

        System.out.println("Shooter Game වින්ඩෝ එක සහ GamePanel එක සාර්ථකව නිර්මාණය විය!");
    }
}