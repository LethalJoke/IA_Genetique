import sun.plugin.javascript.navig4.Layer;

import java.util.Random;

/**
 * Created by Adrien on 14/07/2018.
 */
public class Brain {

    private static int FLayer = 3;
    private static int SLayer = 4;
    private static double MutationChance = 0.1;

    public Double[][] _fMatrix;
    public Double[] _sMatrix;
    public Double[] _b;

    public Brain()
    {
        _fMatrix = new Double[FLayer][SLayer];
        _sMatrix = new Double[SLayer];
        _b = new Double[SLayer + 1];
    }

    public Brain(Brain br)
    {
        _fMatrix = new Double[FLayer][SLayer];
        _sMatrix = new Double[SLayer];
        _b = new Double[SLayer + 1];

        for(int i = 0; i < SLayer; i++)
            for(int j = 0; j < FLayer; j++)
                _fMatrix[j][i] = br._fMatrix[j][i];
        for(int i = 0; i < SLayer; i++)

            _sMatrix[i] = br._sMatrix[i];

        for(int i = 0; i < SLayer + 1; i++)
            _b[i] = br._b[i];
    }

    public void randomize()
    {
        Random r = new Random();
        for(int i = 0; i < SLayer; i++)
            for(int j = 0; j < FLayer; j++)
                _fMatrix[j][i] = r.nextDouble()*2 -1;

        for(int i = 0; i < SLayer; i++)
            _sMatrix[i] = r.nextDouble()*2 -1;

        for(int i = 0; i < SLayer + 1; i++)
            _b[i] = r.nextDouble()*2 -1;
    }

    private static double sigmoid(double x)
    {
        return 1 / (1 + Math.exp(-x));
    }

    //1 if jump, 0 else
    public boolean nextMove(Double[] args)
    {
        Double[] sLayer = new Double[SLayer];
        Double sum;

        for(int i = 0; i < SLayer; i++)
        {
            sum = 0.0;
            for (int j = 0; j < FLayer; j++)
                sum += args[j] * _fMatrix[j][i];
            sLayer[i] = sigmoid(sum + _b[i]);
        }

        sum = _b[SLayer];
        for(int i = 0; i < SLayer; i++)
        {
            sum += sLayer[i] * _sMatrix[i];
        }

        return sum > 0.0;
    }

    public void mutate() {
        Random r = new Random();
        for (int i = 0; i < FLayer * SLayer + 2 * SLayer + 1; i++) {
            if (r.nextDouble() <= MutationChance)
            {
                if(i < SLayer * FLayer)
                {
                    _fMatrix[i/SLayer][i%SLayer] *= (r.nextDouble()*4 -2);
                }
                else if(i < SLayer * FLayer + SLayer)
                {
                    _sMatrix[i-SLayer*FLayer] *= (r.nextDouble()*4 -2);
                }
                else
                {
                    _b[i-(SLayer*FLayer + SLayer)] *= (r.nextDouble()*4 -2);
                }
            }
        }
    }
}
