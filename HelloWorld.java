import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.util.Random;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class HelloWorld {

    private long window;
    private int textureID;
    private int[] textTextureIDs;
    private int pontuacaoTextureID;
    private float imgX = 300, imgY = 200, imgW = 200, imgH = 200;
    private boolean pulando = false;
    private float puloAltura = 20;
    private float puloAtual = 0;
    private int clickCount = 0;

    //quadrado de upgrade 1 = clique de mouse e texto
    private int upgradeTextoTextureID;
    private int upgradeTextoTextureID2;
    private int upgradeTextoTextureID3;
    private int upgradeTextoTextureID4;
    private int upgradeTextoTextureID5;
    private float upgX = 5, upgY = 5, upgW = 100, upgH = 100;
    private int contagemUpg = 1;
    private int custoUpg = 1;
    
    private class Estrela {
        float x, y;
        float r, g, b;
        float tamanho;
        float tempoMudanca;
        
        public Estrela(Random random) {
            x = random.nextFloat() * 800;
            y = random.nextFloat() * 600;
            r = random.nextFloat();
            g = random.nextFloat();
            b = random.nextFloat();
            tamanho = 1.0f + random.nextFloat() * 2.0f;
            tempoMudanca = random.nextFloat() * 4.0f + 1.0f;
        }
        
        public void atualizar(float deltaTime, Random random) {
            tempoMudanca -= deltaTime;
            if (tempoMudanca <= 0) {
                r = random.nextFloat();
                g = random.nextFloat();
                b = random.nextFloat();
                tempoMudanca = random.nextFloat() * 4.0f + 1.0f;
            }
        }
        
        public void renderizar() {
            glPointSize(tamanho);
            glBegin(GL_POINTS);
            glColor3f(r, g, b);
            glVertex2f(x, y);
            glEnd();
            glColor3f(1.0f, 1.0f, 1.0f);
        }
    }
    
    private class TextoFlutuante {
        String texto;
        float x, y;
        float alpha;
        float velocidadeY;
        int textureID;
        
        public TextoFlutuante(String texto, float x, float y, int textureID) {
            this.texto = texto;
            this.x = x;
            this.y = y;
            this.alpha = 1.0f;
            this.velocidadeY = 0.5f;
            this.textureID = textureID;
        }
        
        public boolean atualizar(float deltaTime) {
            y += velocidadeY;
            alpha -= 0.01f * deltaTime * 60;
            return alpha > 0;
        }
        
        public void renderizar() {
            glColor4f(1.0f, 1.0f, 1.0f, alpha);
            
            int[] width = new int[1];
            int[] height = new int[1];
            glBindTexture(GL_TEXTURE_2D, textureID);
            glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, width);
            glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT, height);
            
            float centroImagem = imgX + (imgW / 2);
            float textoX = centroImagem - (width[0] / 2);
            
            renderizarTextura(textureID, textoX, y, 0, 0);
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
    
	private String[] frases = {
		"Clique registrado!",
		"Mais um click!",
		"Vai com calma!",
		"Ta viciado?",
		"Continua clicando!",
		"Bom trabalho!",
		"Click click click!",
		"Voce esta desenhando ou hackeando?",
		"OpenGL agradece sua interacao.",
		"Isso e um clique ou um ataque DDoS?",
		"Compilei ate sua intencao!",
		"Cuidado, esse clique pode causar recursao!",
		"Clique aceito. Proximo frame.",
		"Se clicar mais, vai abrir um buraco no buffer!",
		"Mouse operante, programador contente.",
		"E Java, mas parece magia, ne?",
		"Lembre-se: cada clique conta um pixel na historia.",
		"De tanto clicar, vai dar stack overflow.",
		"Quase um artista grafico digital!",
		"A GPU esta te observando.",
		"Comando recebido. RENDER!",
		"Nem o Blender aguenta esse ritmo.",
		"Se clicar mais, vou ter que alocar mais memoria!",
		"OpenGL nunca viu tanto amor.",
		"Sua taxa de cliques esta melhor que FPS de jogo indie.",
		"Programando com estilo... e cliques!",
		"Esse clique vai direto pro framebuffer.",
		"Java sentiu esse input com forca.",
		"Quase desenhando um Picasso com tanto clique.",
		"Um clique por frame, ta equilibrado.",
		"A JVM gostou disso.",
		"GL_POINTS ativado!",
		"Vertex shader sentiu esse clique!",
		"O mouse pediu arrego.",
		"Mais rapido que renderizacao em Ray Tracing.",
		"Ta tentando clicar no NullPointerException?",
		"Comando executado: desenhar entusiasmo.",
		"Esse clique iluminou a cena!",
		"Clique mais e descubra o easter egg (spoiler: nao tem).",
		"Comportamento interativo detectado.",
		"Daria pra fazer um jogo com sua taxa de clique.",
		"Esse clique foi direto pro pipeline grafico.",
		"Cuidado: cliques em excesso causam bugs existenciais.",
		"OpenGL manda um salve.",
		"Seu clique alterou o universo (em escala local).",
		"JavaFX chora ao ver tanta acao assim.",
		"GPU em choque com tanto evento.",
		"Um clique, mil vertices renderizados.",
		"Clique mais e desbloqueia o Modo Shader Supremo.",
		"Quase criando um fractal de eventos!",
		"Com esse ritmo, vai desenhar ate Mandelbrot.",
		"Ta modelando ou jogando Clicker Heroes?",
		"Se clicar mais, vou ter que usar double buffering.",
		"E bonito ver tanto interesse... em clicar.",
		"Esse clique passou pelo pipeline grafico com louvor.",
		"Ja pensou em virar artista 3D?",
		"OpenGL vai pedir aumento de salario.",
		"Java ta suando aqui com seus eventos.",
		"GL_TRIANGLES? GL_QUADS? Nao. GL_CLICKS!",
		"Renderizando sua vontade em tempo real.",
		"Tanta interacao... quase virou IA!",
		"Desenha mais um clique ai.",
		"Shader compilado so com seu entusiasmo.",
		"Mouse handler cansou de voce.",
		"Clique computado. Proximo!",
		"Ta querendo spawnar um cubo, ne?",
		"Cada clique seu, um `System.out.println()` meu.",
		"Mouse operante = aluno motivado.",
		"Se clicar mais, vai instanciar outro objeto.",
		"Essa textura sentiu o impacto.",
		"Olha o Garbage Collector vindo com raiva.",
		"Nao e Unity, mas e quase um jogo.",
		"Voce desbloqueou: Professor Feliz.",
		"GL_COLOR_BUFFER_BIT limpo, pode clicar mais!",
		"Vamos criar um `ClickRenderer` so pra voce.",
		"Fico feliz em ver tanto `input()` assim.",
		"Daqui a pouco vira um mini Paint.",
		"Esse clique renderizou emocao.",
		"Cuidado! Vai criar uma thread por clique!",
		"Shader sentiu um frio na espinha.",
		"A main loop nao para mais agora.",
		"Se clicar mais, eu dou nota extra.",
		"Interacao grafica de altissimo nivel.",
		"A renderizacao ta chorando de emocao.",
		"Java ta aguentando firme.",
		"Parece que temos um futuro GameDev aqui.",
		"Essa interface nunca foi tao tocada.",
		"Se clicar mais, o mouse vai pra segunda dimensao.",
		"Ta quase desenhando com cliques.",
		"Ate o Paint sentiria inveja.",
		"Vamos implementar fisica nos cliques?",
		"Vai precisar de uma IA pra prever seus cliques.",
		"Ta tentando quebrar o event listener?",
		"Esse evento foi bem tratado, parabens!",
		"E tanto clique que parece benchmark.",
		"Daqui a pouco sai um modelo 3D dai.",
		"To quase plotando isso em tempo real.",
		"Se clicar mais, vira performance art.",
		"Ate o NetBeans ficaria orgulhoso.",
		"A malha de poligonos agradece.",
		"Cuidado com o clique fantasma.",
		"Isso ta mais animado que aula de sexta.",
		"Voce clicou e eu renderizei um elogio."
	};
	
    
    private Random random = new Random();
    private List<Estrela> estrelas = new ArrayList<>();
    private List<TextoFlutuante> textosFlutuantes = new ArrayList<>();
    private long ultimoFrame = 0;
    private float deltaTime = 0.016f;

    public void run() {
        init();
        loop();

        glDeleteTextures(textureID);
        if (textTextureIDs != null) {
            for (int id : textTextureIDs) {
                glDeleteTextures(id);
            }
        }
        if (pontuacaoTextureID > 0) {
            glDeleteTextures(pontuacaoTextureID);
        }
        if (upgradeTextoTextureID > 0){
            glDeleteTextures(upgradeTextoTextureID);
        }
        if (upgradeTextoTextureID2 > 0){
            glDeleteTextures(upgradeTextoTextureID2);
        }
        if (upgradeTextoTextureID3 > 0){
            glDeleteTextures(upgradeTextoTextureID3);
        }
        if (upgradeTextoTextureID4 > 0){
            glDeleteTextures(upgradeTextoTextureID4);
        }
        if (upgradeTextoTextureID5 > 0){
            glDeleteTextures(upgradeTextoTextureID5);
        }
        
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Não foi possível inicializar GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(800, 600, "JLClicker", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Erro ao criar a janela");
		try {
			InputStream iconStream = HelloWorld.class.getResourceAsStream("/res/logo.png");
			if (iconStream != null) {
				BufferedImage iconImage = ImageIO.read(iconStream);
				int width = iconImage.getWidth();
				int height = iconImage.getHeight();
				int[] pixels = new int[width * height];
				iconImage.getRGB(0, 0, width, height, pixels, 0, width);
		
				ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pixel = pixels[y * width + x];
						buffer.put((byte) ((pixel >> 16) & 0xFF));
						buffer.put((byte) ((pixel >> 8) & 0xFF));
						buffer.put((byte) (pixel & 0xFF));
						buffer.put((byte) ((pixel >> 24) & 0xFF));
					}
				}
				buffer.flip();
		
				GLFWImage icon = GLFWImage.malloc();
				icon.set(width, height, buffer);
		
				GLFWImage.Buffer icons = GLFWImage.malloc(1);
				icons.put(0, icon);
		
				glfwSetWindowIcon(window, icons);
		
				icons.free();
				icon.free();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        glfwSetMouseButtonCallback(window, (win, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                DoubleBuffer xb = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yb = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xb, yb);
                double mx = xb.get(0);
                double my = 600 - yb.get(0);

                //Verifica onde cliclou e identifica para saber se está no limite da imagem.png (ponto de inicio da imagem 300x200 dimensionalidade 200x200)
                if (mx >= imgX && mx <= imgX + imgW && my >= imgY && my <= imgY + imgH) {
                    pulando = true;
                    puloAtual = puloAltura;
                    int fraseIndex = random.nextInt(frases.length);
                    float textoY = imgY + imgH + 10;
                    textosFlutuantes.add(new TextoFlutuante(frases[fraseIndex], 0, textoY, textTextureIDs[fraseIndex]));
                    // Atualizando forma de ganhar ponto por conta do upgrade 1
                    // clickCount++;
                    clickCount += contagemUpg;
                    
                    //renderiza toda vez que a imagem.png for clicado, assim atualizando os pontos ao vivo
                    try {
                        pontuacaoTextureID = criarTexturaTexto("Pontos: " + clickCount, 16, Color.WHITE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Em tese, se eu criar um limite aleatorio, posso considerar como 
                //uma hitbox no espaço da tela para adicionar os upgrades

                if (mx >= upgX && mx <= upgX + upgW && my >= upgY && my <= upgY + upgH){
                    if(clickCount >= custoUpg){
                        contagemUpg++;
                        clickCount -= custoUpg;
                        custoUpg = custoUpg * 7 / 3;
                    }
                    try {
                        pontuacaoTextureID = criarTexturaTexto("Pontos: " + clickCount, 16, Color.WHITE);
                        upgradeTextoTextureID4 = criarTexturaTexto("" + contagemUpg, 10, Color.WHITE);
                        upgradeTextoTextureID5 = criarTexturaTexto("Custo: " + custoUpg, 10, Color.WHITE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 800, 0, 600, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        try {
            textureID = criarTextura("/res/imagem.png");
            
            textTextureIDs = new int[frases.length];
            for (int i = 0; i < frases.length; i++) {
                textTextureIDs[i] = criarTexturaTexto(frases[i], 14, Color.WHITE);
            }
            
            pontuacaoTextureID = criarTexturaTexto("Pontos: 0", 16, Color.WHITE);

            //Texto que aparece no quadrado de upgrade
            upgradeTextoTextureID = criarTexturaTexto("Mais", 10, Color.WHITE);
            upgradeTextoTextureID2 = criarTexturaTexto("por", 10, Color.WHITE);
            upgradeTextoTextureID3 = criarTexturaTexto("clique:", 10, Color.WHITE);
            upgradeTextoTextureID4 = criarTexturaTexto("1", 10, Color.WHITE);
            upgradeTextoTextureID5 = criarTexturaTexto("Custo: 1", 10, Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (int i = 0; i < 300; i++) {
            estrelas.add(new Estrela(random));
        }
        
        ultimoFrame = System.currentTimeMillis();
    }

    private void loop() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        while (!glfwWindowShouldClose(window)) {
            long agora = System.currentTimeMillis();
            deltaTime = (agora - ultimoFrame) / 1000.0f;
            ultimoFrame = agora;
            
            glClear(GL_COLOR_BUFFER_BIT);

            for (Estrela estrela : estrelas) {
                estrela.atualizar(deltaTime, random);
                estrela.renderizar();
            }

            if (pulando) {
                imgY += 2;
                puloAtual -= 2;
                if (puloAtual <= 0) {
                    pulando = false;
                    imgY = 200;
                }
            }

            renderizarTextura(textureID, imgX, imgY, imgW, imgH);
            
            for (int i = textosFlutuantes.size() - 1; i >= 0; i--) {
                TextoFlutuante texto = textosFlutuantes.get(i);
                if (!texto.atualizar(deltaTime)) {
                    textosFlutuantes.remove(i);
                } else {
                    texto.renderizar();
                }
            }
            
            if (pontuacaoTextureID > 0) {
                renderizarTextura(pontuacaoTextureID, 10, 550, 0, 0);
            }

            //Renderiza o texto de upgrade
            //Gambiarra a baixo para testar os textos, se possivel criar uma array de upgradeTextoTextureID
            //Ou fazer alguma forma de aceitar quebra de linha para renderizar a imagem do texto na 
            //função de renderizar textura
            //não fui eu que implementei então lkkkkkkkkk
            if (upgradeTextoTextureID > 0) {
                renderizarTextura(upgradeTextoTextureID, 25, 80, 0, 0);
            }
            if (upgradeTextoTextureID2 > 0) {
                renderizarTextura(upgradeTextoTextureID2, 30, 60, 0, 0);
            }
            if (upgradeTextoTextureID3 > 0) {
                renderizarTextura(upgradeTextoTextureID3, 15, 40, 0, 0);
            }
            if (upgradeTextoTextureID4 > 0) {
                renderizarTextura(upgradeTextoTextureID4, 40, 20, 0, 0);
            }
            if (upgradeTextoTextureID5 > 0) {
                renderizarTextura(upgradeTextoTextureID5, 10, 5, 0, 0);
            }

            //Renderizar o quadrado de upgrade
            glBegin(GL_LINE_STRIP);
                glColor3f(1f, 1f, 1f);
                glVertex2f(upgX, upgY);
                glVertex2f(upgX + upgW, upgY);
                glVertex2f(upgX + upgW, upgY + upgH);
                glVertex2f(upgX, upgY + upgH);
                glVertex2f(upgX, upgY);
            glEnd();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void renderizarTextura(int textureID, float x, float y, float w, float h) {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureID);

        if (w == 0 && h == 0) {
            int[] width = new int[1];
            int[] height = new int[1];
            glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, width);
            glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT, height);
            w = width[0];
            h = height[0];
        }

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex2f(x, y);
        glTexCoord2f(1, 0); glVertex2f(x + w, y);
        glTexCoord2f(1, 1); glVertex2f(x + w, y + h);
        glTexCoord2f(0, 1); glVertex2f(x, y + h);
        glEnd();

        glBindTexture(GL_TEXTURE_2D, 0);
        glDisable(GL_TEXTURE_2D);
    }

    private int criarTextura(String caminhoRelativo) throws IOException {
        InputStream stream = HelloWorld.class.getResourceAsStream(caminhoRelativo);
        if (stream == null) {
            System.err.println("Imagem não encontrada: " + caminhoRelativo);
            BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, 200, 200);
            g.dispose();
            return criarTexturaDeImagem(img);
        }

        BufferedImage imagem = ImageIO.read(stream);
        return criarTexturaDeImagem(imagem);
    }
    
    private int criarTexturaDeImagem(BufferedImage imagem) {
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();
        
        AffineTransform transform = AffineTransform.getScaleInstance(1, -1);
        transform.translate(0, -altura);
        AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        imagem = operation.filter(imagem, null);
        
        int[] pixels = new int[largura * altura];
        imagem.getRGB(0, 0, largura, altura, pixels, 0, largura);

        ByteBuffer buffer = BufferUtils.createByteBuffer(largura * altura * 4);
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                int pixel = pixels[y * largura + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        buffer.flip();

        int texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, largura, altura, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glBindTexture(GL_TEXTURE_2D, 0);

        return texID;
    }
    
	private int criarTexturaTexto(String texto, int tamanhoFonte, Color cor) {
		Font font;
		try (InputStream fontStream = HelloWorld.class.getResourceAsStream("/res/Daydream.ttf")) {
			if (fontStream == null) {
				throw new IOException("Fonte não encontrada: /res/daydream_3.ttf");
			}
			font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
			font = font.deriveFont(Font.BOLD, tamanhoFonte);
		} catch (Exception e) {
			e.printStackTrace();
			font = new Font("Arial", Font.BOLD, tamanhoFonte);
		}
	
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setFont(font);  
		FontMetrics fm = g2d.getFontMetrics();
		int largura = fm.stringWidth(texto) + 10; // Aumentado de 2 para 10 para evitar cortes
		int altura = fm.getHeight() + 6; // Aumentado de 2 para 6 para garantir espaço vertical
		g2d.dispose();
	
		img = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, largura, altura);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(font);
		g2d.setColor(cor);
		g2d.drawString(texto, 5, fm.getAscent() + 3); // Ajustado para centralizar melhor verticalmente
		g2d.dispose();
	
		return criarTexturaDeImagem(img);
	}

    public static void main(String[] args) {
        new HelloWorld().run();
    }
}