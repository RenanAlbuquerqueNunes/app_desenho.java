import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class DesenhoApp extends JFrame {
    private boolean modoPincel = false;
    private String formaSelecionada = "Quadrado";
    private Color corAtual = Color.BLACK;
    private int tamanho = 100;
    private int espessura = 5;
    private final CanvasDesenho canvas = new CanvasDesenho();

    public DesenhoApp() {
        super("Desenhar com Formas e Pincel");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel principal de controles com layout vertical
        JPanel painelSuperior = new JPanel();
        painelSuperior.setLayout(new BoxLayout(painelSuperior, BoxLayout.Y_AXIS));

        // Primeira linha: seleção de forma, cor e tamanho
        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] formas = {"Quadrado", "Triângulo", "Círculo", "Pentágono", "Estrela"};
        JComboBox<String> comboFormas = new JComboBox<>(formas);
        comboFormas.addActionListener(e -> formaSelecionada = (String) comboFormas.getSelectedItem());

        JButton btnCor = new JButton("Escolher Cor");
        btnCor.addActionListener(e -> {
            Color novaCor = JColorChooser.showDialog(null, "Escolher Cor", corAtual);
            if (novaCor != null) corAtual = novaCor;
        });

        JSlider sliderTamanho = new JSlider(20, 200, tamanho);
        sliderTamanho.setMajorTickSpacing(40);
        sliderTamanho.setPaintTicks(true);
        sliderTamanho.setPaintLabels(true);
        sliderTamanho.addChangeListener(e -> tamanho = sliderTamanho.getValue());

        linha1.add(new JLabel("Forma:"));
        linha1.add(comboFormas);
        linha1.add(btnCor);
        linha1.add(new JLabel("Tamanho:"));
        linha1.add(sliderTamanho);

        // Segunda linha: espessura e botões de ação
        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JSlider sliderEspessura = new JSlider(1, 20, espessura);
        sliderEspessura.setMajorTickSpacing(5);
        sliderEspessura.setPaintTicks(true);
        sliderEspessura.setPaintLabels(true);
        sliderEspessura.addChangeListener(e -> espessura = sliderEspessura.getValue());

        JButton btnModo = new JButton("Modo: Pincel");
        btnModo.addActionListener(e -> {
            modoPincel = !modoPincel;
            btnModo.setText(modoPincel ? "Modo: Formas" : "Modo: Pincel");
        });

        JButton btnDesenhar = new JButton("Desenhar Forma");
        btnDesenhar.addActionListener(e -> {
            if (!modoPincel) {
                canvas.adicionarForma(new Forma(formaSelecionada, corAtual, tamanho));
            }
        });

        JButton btnLimpar = new JButton("Limpar Tela");
        btnLimpar.addActionListener(e -> canvas.limpar());

        linha2.add(new JLabel("Espessura:"));
        linha2.add(sliderEspessura);
        linha2.add(btnModo);
        linha2.add(btnDesenhar);
        linha2.add(btnLimpar);

        painelSuperior.add(linha1);
        painelSuperior.add(linha2);

        add(painelSuperior, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (modoPincel) {
                    canvas.adicionarPonto(new Ponto(e.getX(), e.getY(), corAtual, espessura));
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DesenhoApp::new);
    }

    // ------------------------ Classes auxiliares ------------------------

    static class Forma {
        String tipo;
        Color cor;
        int tamanho;

        public Forma(String tipo, Color cor, int tamanho) {
            this.tipo = tipo;
            this.cor = cor;
            this.tamanho = tamanho;
        }
    }

    static class Ponto {
        int x, y, espessura;
        Color cor;

        public Ponto(int x, int y, Color cor, int espessura) {
            this.x = x;
            this.y = y;
            this.cor = cor;
            this.espessura = espessura;
        }
    }

    static class CanvasDesenho extends JPanel {
        private final List<Forma> formas = new ArrayList<>();
        private final List<Ponto> pontos = new ArrayList<>();

        public CanvasDesenho() {
            setBackground(Color.WHITE);
        }

        public void adicionarForma(Forma forma) {
            formas.add(forma);
            repaint();
        }

        public void adicionarPonto(Ponto ponto) {
            pontos.add(ponto);
            repaint();
        }

        public void limpar() {
            formas.clear();
            pontos.clear();
            repaint();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            for (Forma forma : formas) {
                g2.setColor(forma.cor);
                int x = getWidth() / 2 - forma.tamanho / 2;
                int y = getHeight() / 2 - forma.tamanho / 2;

                switch (forma.tipo) {
                    case "Quadrado":
                        g2.fillRect(x, y, forma.tamanho, forma.tamanho);
                        break;
                    case "Triângulo":
                        int[] xTri = {x, x + forma.tamanho / 2, x + forma.tamanho};
                        int[] yTri = {y + forma.tamanho, y, y + forma.tamanho};
                        g2.fillPolygon(xTri, yTri, 3);
                        break;
                    case "Círculo":
                        g2.fillOval(x, y, forma.tamanho, forma.tamanho);
                        break;
                    case "Pentágono":
                        Polygon penta = new Polygon();
                        for (int i = 0; i < 5; i++) {
                            penta.addPoint(
                                    (int) (x + forma.tamanho / 2 + forma.tamanho / 2 * Math.cos(i * 2 * Math.PI / 5)),
                                    (int) (y + forma.tamanho / 2 + forma.tamanho / 2 * Math.sin(i * 2 * Math.PI / 5))
                            );
                        }
                        g2.fillPolygon(penta);
                        break;
                    case "Estrela":
                        Polygon estrela = new Polygon();
                        for (int i = 0; i < 10; i++) {
                            double angle = Math.PI / 5 * i;
                            int r = (i % 2 == 0) ? forma.tamanho / 2 : forma.tamanho / 4;
                            estrela.addPoint(
                                    (int) (x + forma.tamanho / 2 + r * Math.cos(angle)),
                                    (int) (y + forma.tamanho / 2 + r * Math.sin(angle))
                            );
                        }
                        g2.fillPolygon(estrela);
                        break;
                }
            }

            for (Ponto ponto : pontos) {
                g2.setColor(ponto.cor);
                g2.fillOval(ponto.x - ponto.espessura / 2, ponto.y - ponto.espessura / 2, ponto.espessura, ponto.espessura);
            }
        }
    }
}
