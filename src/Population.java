import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Adrien on 14/07/2018.
 */
public class Population {
    private static int _individuals = 100;
    private static int _delta = 20;
    private static int _spawnRate = 2000;
    private static int _width = 500;
    private static int _height = 500;
    private static int _flappyx = 25;
    private static int _flappyradius = 10;

    private static int _seedchange = 5;

    private int _currentgen;
    private ArrayList<Game> _players;
    private Timeline _obstacles;
    private Timeline _gameloop;
    private long _seed;
    private Stage _stage;
    private double _score;
    public PipeCollection _pipes;

    private Pane _canvas;
    private Text _txt;

    public Population(Stage stage)
    {
        _currentgen = 1;
        _seed = System.currentTimeMillis();
        _players = new ArrayList<>(_individuals);
        _stage = stage;
        _canvas = new Pane();
        _txt = new Text();

        Scene scene = new Scene(_canvas, _width, _height, Color.ALICEBLUE);
        _stage.setResizable(false);
        _stage.setFullScreen(false);
        _stage.setScene(scene);
    }

    public void initialize()
    {
        _pipes = new PipeCollection(_height, _width, _seed);

        for(int i = 0; i< _individuals; i++)
        {
            _players.add(new Game(_width,_height, _flappyx, _flappyradius));
            _players.get(i)._brain.randomize();
        }

        _gameloop = new Timeline(new KeyFrame(Duration.millis(_delta),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        for(Game g : _players)
                        {
                            if (!g._flappy._dead) {
                                g._flappy._dead = checkCollision(g);

                                if (!g._flappy._dead) {
                                    if (g._flappy._jumpcd == 0 && g._brain.nextMove(inputNeurons(g)))
                                        g._flappy.jump();

                                    if (!g._flappy.move(_delta / 1000.0))
                                        _canvas.getChildren().remove(g._flappy);
                                }
                                else
                                    _canvas.getChildren().remove(g._flappy);
                            }
                        }

                        if(checkAllDead())
                        {
                            stop();
                            nextGen();
                        }
                        else {
                            _pipes.move(_delta / 1000.0);
                            _score += _delta / 1000.0;
                            _txt.setText( "Génération : " + _currentgen + "\nScore : "+ (int)(_score * 100) + "\nBirbs alive : " + flappiesalive());
                        }
                    }
                }));
        _gameloop.setCycleCount(Timeline.INDEFINITE);

        _obstacles = new Timeline(new KeyFrame(Duration.millis(_spawnRate),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        if(!checkAllDead())
                        {
                            _pipes.newPipe();
                            _canvas.getChildren().addAll(0,_pipes.last().returnDrawables());
                            Pipe p =_pipes.first();
                            if(p._lower.getLayoutX() < -100)
                            {
                                _canvas.getChildren().remove(p._lower);
                                _canvas.getChildren().remove(p._upper);
                                _canvas.getChildren().remove(p._number);
                                _pipes.removeFirst();
                            }
                        }
                    }
                }));
        _obstacles.setCycleCount(Timeline.INDEFINITE);
    }

    private Double[] inputNeurons(Game g)
    {
        Double[] param = new Double[3];
        //Dist Flappy <-> Tuyau / Width
        param[0] = ( _pipes.getNextPipePos(_flappyx - _flappyradius) - _flappyx) / _width;

        // Hauteur Tuyau - Flappy/ Height
        param[1] = ( new Double(_pipes.getNextPipeMid(_flappyx - _flappyradius)) - g._flappy.getCenterY()) / _height;

        //% hauteur
        param[2] = g._flappy.getCenterY() / _height;
        return param;
    }

    public void start()
    {
        _gameloop.playFromStart();
        _obstacles.playFromStart();
    }

    private void stop()
    {
        _gameloop.stop();
        _obstacles.stop();
    }

    private int findFather(long number, ArrayList<Long> cumul)
    {
        int it = 0;
        while(it < _individuals && cumul.get(it) < number)
            it++;
        return (it-1);
    }

    private void nextGen()
    {
        int mid = _pipes.getNextPipeMid(_flappyx - _flappyradius);

        Game g = getFittest(mid);
        //Game h = getWorst(mid);
        System.out.println("** Génération finie : " + _currentgen + " **");
        System.out.println("Best -> " + g.score());
        //System.out.println("Worst : " + h.details(mid));

        //Copie du meilleur cerveau, non-régression
        Brain best = new Brain(g._brain);

        ArrayList<Long> cumulfitness = new ArrayList<>(_individuals);
        cumulfitness.add(0L);
        for(int i = 1; i < _individuals; i++)
        {
            cumulfitness.add(cumulfitness.get(i-1) +  _players.get(i-1).fitness(mid));
        }

        long total = cumulfitness.get(cumulfitness.size()-1) + _players.get(_individuals-1).fitness(mid);
        Random r = new Random();
        ArrayList<Game> nextplayers = new ArrayList<>(_individuals);

        for(int i = 0; i < _individuals; i++)
        {
            long fatherscore = r.nextLong() % total;
            while(fatherscore < 0L)
                fatherscore += total;
            nextplayers.add(new Game(_width, _height, _flappyx, _flappyradius));
            nextplayers.get(i)._brain = new Brain(_players.get(findFather(fatherscore, cumulfitness))._brain);
            nextplayers.get(i)._brain.mutate();
        }

        nextplayers.get(_individuals - 1)._brain = new Brain(best);
        _players = nextplayers;

        resetAll();
        start();
        show();
    }

    private boolean collides(Flappy c, Pipe p) {
        if(p._lower.getLayoutX() > c.getCenterX() + c.getRadius())
            return false;
        if(p._lower.getLayoutX() + p._lower.getWidth() < c.getCenterX() - c.getRadius())
            return false;
        if(p._mid + p._gap/2 > c.getCenterY() + c.getRadius() && p._mid - p._gap/2 < c.getCenterY() - c.getRadius())
            return false;
        return true;
    }

    private int flappiesalive()
    {
        int i = 0;
        for(Game g : _players)
            if(!g._flappy._dead)
                i++;
        return i;
    }


    public boolean checkCollision(Game g)
    {
        for(Pipe p : _pipes._pipes)
        {
            if(collides(g._flappy, p))
                return true;
        }
        return false;
    }

    private boolean checkAllDead()
    {
        for(Game g : _players)
            if(!g._flappy._dead)
                return false;
        return true;
    }

    private Game getFittest(int mid)
    {
        Game best = _players.get(0);
        long currentmax = _players.get(0).fitness(mid);
        for(Game g : _players)
            if(g.fitness(mid) > currentmax)
            {
                currentmax = g.fitness(mid);
                best = g;
            }
        return best;
    }

    private Game getWorst(int mid)
    {
        Game worst = _players.get(0);
        long currentmin = _players.get(0).fitness(mid);
        for(Game g : _players)
            if(g.fitness(mid) < currentmin)
            {
                currentmin = g.fitness(mid);
                worst = g;
            }
        return worst;
    }

    private void resetAll()
    {
        _currentgen++;
        _score = 0.0;
        if(_currentgen % _seedchange == 1)
            _seed = System.currentTimeMillis();
        _pipes = new PipeCollection(_height, _width, _seed);
        for(Game g : _players)
            g._flappy = new Flappy(_height, _flappyx, _flappyradius);
        _players.get(_individuals - 1)._flappy.setFill(Color.RED);
    }

    public void show()
    {
        _canvas.getChildren().clear();
        _txt.relocate(10,10);
        for(Game g : _players)
            if(!g._flappy._dead)
                _canvas.getChildren().add(g._flappy);
        _canvas.getChildren().add(_txt);
        _stage.setTitle("Flappy Birb AI - Gen " + _currentgen);
        _stage.show();
    }

}
