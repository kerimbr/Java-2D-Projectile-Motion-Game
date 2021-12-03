import javax.swing.*;
import java.awt.*;

public class App extends JFrame {

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();


    public App(){
        int width = (int) (screenSize.width * 0.8);
        int height = (int) (screenSize.height * 0.8);

        add(new Game(width,height));
        setSize(width,height);
        setTitle("Atış ve Çarpışma");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                App app = new App();
                app.setVisible(true);
            }
        });
    }


}
