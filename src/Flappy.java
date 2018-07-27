import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by Adrien on 13/07/2018.
 */
public class Flappy extends Circle {

    private static double _gravity = 65;
    private static double _upvelocity = 570;
    private static double _height;
    private static double _maxspeed = 320;
    private static int _JCD = 10;

    private double _currentvelocity;
    private double _score;
    public boolean _dead;
    public int _jumpcd;
    private int _radius;

    public Flappy(int height, int flappyx, int flappyradius)
    {
        super(flappyradius, Color.ORANGE);
        _radius = flappyradius;
        setCenterX(flappyx);
        setCenterY(height /3);
        _currentvelocity = 0.0;
        _height = height;
        _score = 0;
        _dead = false;
        _jumpcd = 0;
    }

    public boolean move(double delta)
    {
        _currentvelocity += _gravity;

        if(_currentvelocity > _maxspeed)
            _currentvelocity = _maxspeed;

        setCenterY(getCenterY() + delta * _currentvelocity);

        if(getCenterY() < 0)
            _dead = true;

        if(getCenterY() > _height - _radius)
            _dead = true;

        if(!_dead)
            _score += delta;

        if(_jumpcd > 0)
            _jumpcd--;

        return !_dead;
    }

    public void jump()
    {
        _currentvelocity = -_upvelocity;
        _jumpcd = _JCD;
    }

    public double getScore()
    {
        return  _score;
    }


}
