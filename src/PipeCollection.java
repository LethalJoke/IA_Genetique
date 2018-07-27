
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Adrien on 13/07/2018.
 */

class Pipe{
    private static double _scrolling = 200;

    public Rectangle _upper;
    public Rectangle _lower;
    public Text _number;
    public int _mid;
    public int _gap;
    public boolean _removable;
    public int _pipewidth;

    public Pipe(int mid, int gap, int height, int width, int number, int pipewidth)
    {
        _mid = mid;
        _gap = gap;
        _pipewidth = pipewidth;

        _number = new Text();
        _upper = new Rectangle(_pipewidth, mid - gap/2, Color.GREEN);
        _lower = new Rectangle(_pipewidth, height - mid - gap/2, Color.GREEN);
        _upper.relocate(width + _pipewidth, 0);
        _lower.relocate(width + _pipewidth, height - _lower.getHeight());
        _number.setText(Integer.toString(number));
        _number.setFill(Color.WHITE);
        _number.setFont(Font.font ("Verdana", 20));
        _number.relocate(width + _pipewidth, mid - gap/2 - 40);
        _removable = false;
    }

    public void move(double delta)
    {
        double x =_upper.getLayoutX() - _scrolling * delta;
        _upper.setLayoutX(x);
        _lower.setLayoutX(x);
        _number.setLayoutX(x);
        _removable = x < -_pipewidth;
    }

    public ArrayList<Shape> returnDrawables()
    {
        ArrayList<Shape> children = new ArrayList<>();
        children.add(_lower);
        children.add(_upper);
        children.add(_number);
        return children;
    }
}

public class PipeCollection{

    private static int _gapwidth = 100;
    private static int _baseheight = 150;
    private static int _scaleheight = 200;
    private static int _pipewidth = 40;

    private int _height;
    private int _width;
    public ArrayList<Pipe> _pipes;
    private Random _random;
    private int _currentpipe;

    public PipeCollection(int height, int width, long seed)
    {
        _height = height;
        _width = width;
        _pipes = new ArrayList<>();
        _random = new Random(seed);
        _currentpipe = 0;
    }

    public Pipe first()
    {
        return _pipes.get(0);
    }

    public Pipe last()
    {
        return _pipes.get(_pipes.size()-1);
    }

    public void removeFirst()
    {
        _pipes.remove(0);
    }

    public void newPipe()
    {
        _currentpipe++;
        _pipes.add(new Pipe( (int)(_random.nextFloat() *_scaleheight) + _baseheight, _gapwidth, _height, _width, _currentpipe, _pipewidth));
    }

    public void move(double delta)
    {
        for(Pipe p : _pipes)
            p.move(delta);
    }

    public int getNextPipeMid(int minimumx)
    {
        int x = _height / 2;
        if (_pipes.size() > 0 && minimumx <= _pipes.get(0)._lower.getLayoutX()+  _pipewidth)
            x= _pipes.get(0)._mid;
        else if(_pipes.size() > 1)
            x= _pipes.get(0)._mid;
        return x;
    }

    public Double getNextPipePos(int minimumx)
    {
        Double w = (double) _width;
        if (_pipes.size() > 0 && minimumx <= _pipes.get(0)._lower.getLayoutX()+  _pipewidth)
            w= _pipes.get(0)._lower.getLayoutX();
        else if(_pipes.size() > 1)
            w= _pipes.get(1)._lower.getLayoutX();
        return w;
    }


}
