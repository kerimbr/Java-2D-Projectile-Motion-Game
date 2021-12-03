import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends JPanel implements ActionListener,KeyListener {

    private int width;
    private int height;

    // Ölçüler
    int terrainHeight;

    int moundHeight;
    int moundWidth;

    int ballPositionX;
    int ballPositionY;
    int ballRadius;

    int enemyPositionX;
    int enemyPositionY;
    int enemySize;

    double angle;
    double angleRadian;

    int force;
    int velocityX;
    int velocityY;

    double deltaTime;
    double gravity = 9.8;


    // Nesneler
    Ellipse2D ball;
    Color speedTextColor;
    Rectangle2D enemy;

    //Mantıksal İfadeler
    boolean isSetAngle;
    boolean isMotion;
    boolean isShowEnemy;

    //Zamanlayıcılar
    private SpeedTimer speedTimer;
    private Timer ballTimer;



    public Game(int width, int height){
        this.width = width;
        this.height = height;

        addKeyListener(this);
        setFocusable(true);

        ballTimer = new Timer(1000/60,this);
        speedTimer = new SpeedTimer();


        initGameSettings();

    }

    private void initGameSettings() {
        //Init.
        isSetAngle = true;
        isMotion = false;
        isShowEnemy = true;

        angle = 45;
        force = 10;

        terrainHeight = (int)(height - height *0.15);
        moundHeight = (int) (height / 5);
        moundWidth = (int) (width / 10);

        ballRadius = 50;
        ballPositionX = (int) (width *0.05);
        ballPositionY = terrainHeight - (moundHeight + ballRadius/2);

        angleRadian = Math.toRadians(angle);

        enemySize = 70;
        enemyPositionX = ThreadLocalRandom.current().nextInt((int) (width *0.5),(int) (width *0.9));
        enemyPositionY = ThreadLocalRandom.current().nextInt(0, ballPositionY-enemySize);

        deltaTime = 0.1;
    }


    // Draw Methods

    private void doDrawing(Graphics g){

        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);


        drawMap(g2d);

        if (!isMotion) drawArrow(g2d);


        drawBall(g2d);

        drawDashboard(g2d);

        if(isShowEnemy) drawEnemy(g2d);

        g2d.dispose();
    }

    private void drawEnemy(Graphics2D g2d) {
        enemy = new Rectangle2D.Float(enemyPositionX,enemyPositionY,enemySize,enemySize);
        g2d.setColor(Color.RED);
        g2d.fill(enemy);
    }

    private void drawDashboard(Graphics2D g2d) {

        g2d.drawString("Press 'R' to Restart",50,50);

        if (isSetAngle){

            g2d.setColor(new Color(23, 22, 22, 169));

            g2d.setFont(new Font("TimesRoman",Font.BOLD,40));
            g2d.drawString((int)angle + "°",width/2,height/3);

        }else{
            g2d.setFont(new Font("TimesRoman",Font.BOLD,40));

            g2d.setColor(new Color(23, 22, 22, 169));
            g2d.drawString((int)angle + "°",width/2,height/3-50);

            g2d.setColor(speedTextColor);
            g2d.drawString((int)force + " m/s",width/2 - 30,height/3);


        }

    }

    private void drawArrow(Graphics2D g2d) {

        float[] dash = {4f, 0f, 2f};
        BasicStroke basicStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 1.0f, dash, 2f);

        g2d.setStroke(basicStroke);
        g2d.setColor(Color.DARK_GRAY);

        int arrowX = ballPositionX;
        int arrowY = ballPositionY;

        Point2D x1y1 = new Point2D.Float(arrowX,arrowY);

        arrowX = (int) (arrowX + 80 * Math.cos(Math.toRadians(angle)));
        arrowY = (int) (arrowY - 80 * Math.sin(Math.toRadians(angle)));

        Point2D x2y2 = new Point2D.Float(arrowX,arrowY);
        Line2D arrow = new Line2D.Float(x1y1,x2y2);

        g2d.draw(arrow);

    }

    private void drawBall(Graphics2D g2d) {
        Color ballColor = new Color(33,71,205);
        g2d.setColor(ballColor);
        ball = getEllipseFromCenter(ballPositionX,ballPositionY,ballRadius,ballRadius);
        g2d.fill(ball);
    }

    private void drawMap(Graphics2D g2d) {

        Rectangle2D terrain = new Rectangle2D.Float(0,terrainHeight,width,terrainHeight);
        Color terrainColor = new Color(52,140,49);
        g2d.setColor(terrainColor);
        g2d.fill(terrain);


        Rectangle2D mound = new Rectangle2D.Float(0,terrainHeight - moundHeight, moundWidth,moundHeight);
        Color moundColor = new Color(40, 19, 2);
        g2d.setColor(moundColor);
        g2d.fill(mound);
    }


    private Ellipse2D getEllipseFromCenter(double x, double y, double width, double height) {
        double newX = x - width / 2.0;
        double newY = y - height / 2.0;
        return new Ellipse2D.Double(newX, newY, width, height);
    }


    // Override Methods

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        deltaTime += 0.1;
        ballPositionX = (int) (ballPositionX + velocityX );
        ballPositionY = (int) (ballPositionY - velocityY + gravity*deltaTime);


        if (ballPositionY >= terrainHeight-ballRadius/3){
            ballTimer.stop();

            if (isShowEnemy) {
                int dialogResult = JOptionPane.showConfirmDialog(
                        null,
                        "Vuramadın :(",
                        "Tekrar Dene",
                        JOptionPane.YES_NO_OPTION
                );

                if (dialogResult == JOptionPane.YES_OPTION) {
                    restartGame();
                }
            }
        }

        if (isShowEnemy) {
            if (enemy.intersects(ball.getBounds2D())) {
                isShowEnemy = false;
                repaint();
                int dialogResult = JOptionPane.showConfirmDialog(
                        null,
                        "Tebrikler Hedefi Vurdun *-* \n Tekrar Denemek İster misin ?",
                        "Tebrikler",
                        JOptionPane.YES_NO_OPTION
                );
                if (dialogResult == JOptionPane.OK_OPTION) {
                    speedTimer.stopTimer();
                    ballTimer.stop();
                    restartGame();
                }
            }
        }
        repaint();
    }

    private void restartGame() {
        initGameSettings();
        repaint();
    }


    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_R){
            speedTimer.stopTimer();
            ballTimer.stop();
            restartGame();
        }

        if (isSetAngle) {

            if (e.getKeyCode() == KeyEvent.VK_UP) {
                angle++;
                if (angle > 75) {
                    angle = 10;
                }
                angleRadian = Math.toRadians(angle);
                repaint();
            }

            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                angle--;
                if (angle < 10) {
                    angle = 75;
                }
                angleRadian = Math.toRadians(angle);
                repaint();
            }

            if (e.getKeyCode() == KeyEvent.VK_SPACE){
                isSetAngle = false;
                speedTimer.startTimer();
                repaint();
            }

        }else{

            if (e.getKeyCode() == KeyEvent.VK_SPACE){
                speedTimer.stopTimer();
                isMotion = true;
                velocityX = (int) (force * Math.cos(angleRadian));
                velocityY = (int) (force * Math.sin(angleRadian));
                ballTimer.start();
                repaint();
            }

        }

    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}


    // Nested Class
    class SpeedTimer implements ActionListener{

        private Timer timer;
        private int delay = 1000/60;

        public SpeedTimer(){
            timer = new Timer(delay,this);
        }

        public void startTimer(){
            timer.start();
        }

        public void stopTimer(){
            timer.stop();
        }

        public void restartTimer(){
            timer.restart();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isSetAngle && !isMotion){

                force ++;

                if(force > 100){
                    force = 10;
                }

                if (force > 75){
                    speedTextColor = new Color(255, 38, 38, 169);
                }else if (force > 50 && force < 75){
                    speedTextColor = new Color(255, 148, 1, 169);
                }else if (force > 25 && force < 50){
                    speedTextColor = new Color(6, 255, 6, 169);
                }else{
                    speedTextColor = new Color(23, 22, 22, 169);
                }

                repaint();


            }
        }
    }

}


