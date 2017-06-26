package com.thiago.flappybirdy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
    private Random numeroRandomico;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle retqanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;
    private Texture gameOver;
    /*private ShapeRenderer shape;*/

	//Atributos de configurações

	private float larguradispositivo;
	private float alturadispositivo;
    private int estadoDoJogo =0;// 0->Jogo nao Iniciado e 1-> Jogo Inciado
    private int pontuacao=0;


	private float variacao =0;
	private float velocidadeQueda =0;
	private float posicaoInicialVertical;
    private Texture canoBaixo;
    private Texture canoTopo;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private float alturaEntreCanosRandomica;
    private boolean marcouPonto;
    private float posicaoPassaro;

    //camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;





	@Override
	public void create () {

		batch = new SpriteBatch();
        numeroRandomico = new Random();
        passaroCirculo = new Circle();
        /*retanguloCanoBaixo = new Rectangle();
        retqanguloCanoTopo = new Rectangle();
        shape = new ShapeRenderer();*/

        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");
        gameOver = new Texture("game_over.png");

        /***************************************
         * Configuração da Câmera
          */
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);




        larguradispositivo = VIRTUAL_WIDTH;
		alturadispositivo = VIRTUAL_HEIGHT;


		posicaoInicialVertical = alturadispositivo / 2;
        posicaoMovimentoCanoHorizontal = larguradispositivo;
        espacoEntreCanos = 300;
        posicaoPassaro = larguradispositivo/3;


	}

	@Override
	public void render () {

        camera.update();

        //Limpar frames

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime *10;
        if (variacao>2) variacao=0;

        if (estadoDoJogo == 0){

            if (Gdx.input.justTouched()){
                estadoDoJogo = 1;
            }
        }else {//inicia Jogo
            velocidadeQueda ++;
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
                posicaoInicialVertical = posicaoInicialVertical	- velocidadeQueda;

            if (estadoDoJogo ==1){
                posicaoMovimentoCanoHorizontal -= deltaTime *300;

                if (Gdx.input.justTouched()	){
                    velocidadeQueda = -17;
                    }
                //verifica se o cano saiu inteiramente da tela
                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguradispositivo;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                    marcouPonto=false;

                }

                //verifica pontuação
                if (posicaoMovimentoCanoHorizontal < posicaoPassaro) {
                    ;
                    if (!marcouPonto) {
                        pontuacao++;
                        marcouPonto = true;
                    }

                }
            }else {//Tela de Game Over

                    if (Gdx.input.justTouched()){

                        estadoDoJogo = 0;
                        pontuacao =0;
                        velocidadeQueda = 0;
                        posicaoInicialVertical = alturadispositivo /2;
                        posicaoMovimentoCanoHorizontal = larguradispositivo;
                    }
            }
        }

    //Configurar dados de Projeção da câmera
    batch.setProjectionMatrix( camera.combined );

	batch.begin();

	batch.draw(fundo, 0, 0, larguradispositivo , alturadispositivo );
    batch.draw(canoTopo, posicaoMovimentoCanoHorizontal,alturadispositivo/2 + espacoEntreCanos/2+alturaEntreCanosRandomica);
    batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturadispositivo /2- canoBaixo.getHeight() -espacoEntreCanos/2+alturaEntreCanosRandomica);
	batch.draw(passaros [ (int) variacao ], posicaoPassaro, posicaoInicialVertical);
    fonte.draw(batch, String.valueOf(pontuacao),larguradispositivo/2,alturadispositivo - 100);

    if (estadoDoJogo ==2){
        batch.draw(gameOver, larguradispositivo/2-gameOver.getWidth()/2, alturadispositivo/2);
        mensagem.draw(batch,"Toque para Reiniciar!", larguradispositivo/2-205, alturadispositivo/2-gameOver.getHeight()/2);
    }

	batch.end();

        passaroCirculo.set(posicaoPassaro+passaros[0].getWidth()/2, posicaoInicialVertical+passaros[0].getHeight()/2,passaros[0].getWidth()/2);
        retanguloCanoBaixo = new Rectangle(
                posicaoMovimentoCanoHorizontal,alturadispositivo /2- canoBaixo.getHeight() -espacoEntreCanos/2+alturaEntreCanosRandomica,
                canoBaixo.getWidth(), canoBaixo.getHeight()
        );
        retqanguloCanoTopo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturadispositivo/2 + espacoEntreCanos/2+alturaEntreCanosRandomica,
                canoTopo.getWidth(), canoTopo.getHeight()

        );

        //desenhar formas
        /*shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
        shape.rect(retqanguloCanoTopo.x, retqanguloCanoTopo.y, retqanguloCanoTopo.width, retqanguloCanoTopo.height);
        shape.setColor(Color.RED);
        shape.end();*/

        //teste de colisão
        if (Intersector.overlaps(passaroCirculo, retanguloCanoBaixo)|| Intersector.overlaps(passaroCirculo, retqanguloCanoTopo)
               || posicaoInicialVertical <= 0  || posicaoInicialVertical >= alturadispositivo ){
            estadoDoJogo = 2;
        }
	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
