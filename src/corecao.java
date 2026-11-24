import java.util.Scanner;
import java.util.Arrays;

public class corecao {

    // --- DADOS DOS ELEITORES ---
    // RA fictício para o aluno usar. Exemplo: "9999"
    static final String RA_ALUNO = "9999";
    static final String[] ELEITORES_VALIDOS = {"1001", "1002", "1003", "1004", RA_ALUNO};

    // Estrutura para armazenar os votos de cada eleitor (5 eleitores x 5 cargos)
    // Cargos: 0=Federal, 1=Estadual, 2=Senador, 3=Governador, 4=Presidente
    static int[][] votosEleitores = new int[5][5];
    static boolean[] eleitoresVotaram = new boolean[5]; // false = não votou, true = votou

    // --- DADOS DOS CANDIDATOS ---
    // Arrays paralelos: índice do candidato corresponde ao índice nos arrays de nome/numero
    static final String[] NOME_DEPUTADO_FEDERAL = {"José Bombinha", "Ana Silva", "Carlos Lima"};
    static final int[] NUM_DEPUTADO_FEDERAL = {101, 102, 103};

    static final String[] NOME_DEPUTADO_ESTADUAL = {"Taffe da Galera", "Rodrigo Pato Depenado", "Mariana Costa"};
    static final int[] NUM_DEPUTADO_ESTADUAL = {201, 202, 203};

    static final String[] NOME_SENADOR = {"Huilson Clone", "Juliana Rocha", "Roberto Martins"};
    static final int[] NUM_SENADOR = {301, 302, 303};

    static final String[] NOME_GOVERNADOR = {"Patrick Cobra", "Fernanda Souza", "Ricardo Oliveira"};
    static final int[] NUM_GOVERNADOR = {401, 402, 403};

    static final String[] NOME_PRESIDENTE = {"SEU NOME", "Alessandro das Redes", "Lucas Ferreira"};
    static final int[] NUM_PRESIDENTE = {501, 502, 503};

    static final String[] NOMES_CARGOS = {"Deputado Federal", "Deputado Estadual", "Senador", "Governador", "Presidente"};

    // Contadores de votos por cargo e candidato
    static int[][] totalVotos = new int[5][3];

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            opcao = exibirMenu(scanner);

            switch (opcao) {
                case 1:
                    iniciarVotacao(scanner);
                    break;
                case 2:
                    consultarVotosEleitor(scanner);
                    break;
                case 3:
                    exibirResultado();
                    break;
                case 4:
                    mostrarVencedores();
                    break;
                case 0:
                    System.out.println("Encerrando sistema eleitoral. Obrigado!");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
            System.out.println(); // Linha em branco para separar
        } while (opcao != 0);

        scanner.close();
    }

    // --- MENU PRINCIPAL ---
    public static int exibirMenu(Scanner scanner) {
        System.out.println("========== SISTEMA ELEITORAL ==========");
        System.out.println("1 - Iniciar votação");
        System.out.println("2 - Consultar votos de um eleitor");
        System.out.println("3 - Exibir resultado");
        System.out.println("4 - Mostrar vencedores");
        System.out.println("0 - Encerrar");
        System.out.print("Escolha: ");

        while (!scanner.hasNextInt()) {
            System.out.print("Digite um número válido: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    // --- 1. INICIAR VOTAÇÃO ---
    public static void iniciarVotacao(Scanner scanner) {
        System.out.println("--- Iniciar Votação ---");
        System.out.print("Digite seu número do eleitor: ");
        String numeroEleitor = scanner.next();

        // Validação 1: Eleitor autorizado?
        int indexEleitor = indexOf(ELEITORES_VALIDOS, numeroEleitor);
        if (indexEleitor == -1) {
            System.out.println("Eleitor não cadastrado! Votação cancelada.");
            return;
        }

        // Validação 2: Eleitor já votou?
        if (eleitoresVotaram[indexEleitor]) {
            System.out.println("Este eleitor já votou anteriormente!");
            return;
        }

        System.out.println("Eleitor " + numeroEleitor + " identificado. Votando nos cargos:");

        // Coleta de votos para cada um dos 5 cargos
        for (int cargo = 0; cargo < 5; cargo++) {
            int voto;
            boolean votoValido = false;

            while (!votoValido) {
                exibirCandidatosCargo(cargo);
                System.out.print("Voto para " + NOMES_CARGOS[cargo] + ": ");

                if (scanner.hasNextInt()) {
                    voto = scanner.nextInt();

                    // Validação 3: Número corresponde ao cargo?
                    if (isNumeroValidoCargo(voto, cargo)) {
                        votosEleitores[indexEleitor][cargo] = voto;
                        votoValido = true;
                    } else {
                        System.out.println("Número inválido para este cargo! Tente novamente.");
                    }
                } else {
                    System.out.println("Digite um número válido!");
                    scanner.next(); // consome entrada inválida
                }
            }
        }

        // Marca eleitor como votante
        eleitoresVotaram[indexEleitor] = true;
        System.out.println("Votação registrada com sucesso para o eleitor " + numeroEleitor + "!");

        // Atualiza contador global de votos
        atualizarContadoresVotos(indexEleitor);
    }

    // --- 2. CONSULTAR VOTOS DE UM ELEITOR ---
    public static void consultarVotosEleitor(Scanner scanner) {
        System.out.println("--- Consultar Votos ---");
        System.out.print("Digite o número do eleitor: ");
        String numeroEleitor = scanner.next();

        int indexEleitor = indexOf(ELEITORES_VALIDOS, numeroEleitor);

        if (indexEleitor == -1) {
            System.out.println("Eleitor não cadastrado.");
            return;
        }

        if (!eleitoresVotaram[indexEleitor]) {
            System.out.println("Este eleitor ainda não votou.");
            return;
        }

        System.out.println("Votos do eleitor " + numeroEleitor + ":");
        for (int cargo = 0; cargo < 5; cargo++) {
            int voto = votosEleitores[indexEleitor][cargo];
            String nomeCandidato = getCandidatoNome(voto, cargo);
            System.out.println(NOMES_CARGOS[cargo] + ": " + voto + " - " + nomeCandidato);
        }
    }

    // --- 3. EXIBIR RESULTADO ---
    public static void exibirResultado() {
        System.out.println("--- Resultado da Votação ---");
        int totalVotantes = countVotantes();

        if (totalVotantes == 0) {
            System.out.println("Ainda não existem votos registrados.");
            return;
        }

        for (int cargo = 0; cargo < 5; cargo++) {
            System.out.println("\n" + NOMES_CARGOS[cargo] + ":");
            for (int candidato = 0; candidato < 3; candidato++) {
                String nome;
                int numero;
                switch(cargo) {
                    case 0: nome = NOME_DEPUTADO_FEDERAL[candidato]; numero = NUM_DEPUTADO_FEDERAL[candidato]; break;
                    case 1: nome = NOME_DEPUTADO_ESTADUAL[candidato]; numero = NUM_DEPUTADO_ESTADUAL[candidato]; break;
                    case 2: nome = NOME_SENADOR[candidato]; numero = NUM_SENADOR[candidato]; break;
                    case 3: nome = NOME_GOVERNADOR[candidato]; numero = NUM_GOVERNADOR[candidato]; break;
                    default: nome = NOME_PRESIDENTE[candidato]; numero = NUM_PRESIDENTE[candidato]; break;
                }
                System.out.println("  " + numero + " - " + nome + ": " + totalVotos[cargo][candidato] + " votos");
            }
        }
    }

    // --- 4. MOSTRAR VENCEDORES ---
    public static void mostrarVencedores() {
        System.out.println("--- Vencedores ---");

        if (countVotantes() == 0) {
            System.out.println("Ainda não há votos para apurar.");
            return;
        }

        // Regra: Só pode mostrar vencedor se TODAS as 5 pessoas tiverem votado
        if (countVotantes() < 5) {
            System.out.println("É necessário que todas as 5 pessoas tenham votado para mostrar os vencedores.");
            return;
        }

        for (int cargo = 0; cargo < 5; cargo++) {
            System.out.println("\n" + NOMES_CARGOS[cargo] + ":");

            // Encontrar maior votação
            int maxVotos = -1;
            for (int c = 0; c < 3; c++) {
                if (totalVotos[cargo][c] > maxVotos) {
                    maxVotos = totalVotos[cargo][c];
                }
            }

            if (maxVotos == 0) {
                System.out.println("Empate técnico: Nenhum voto foi dado.");
                continue;
            }

            // Identificar todos com maxVotos (para empates)
            int candidatosEmpatados = 0;
            String nomesEmpatados = "";

            for (int c = 0; c < 3; c++) {
                if (totalVotos[cargo][c] == maxVotos) {
                    candidatosEmpatados++;
                    String nome;
                    switch(cargo) {
                        case 0: nome = NOME_DEPUTADO_FEDERAL[c]; break;
                        case 1: nome = NOME_DEPUTADO_ESTADUAL[c]; break;
                        case 2: nome = NOME_SENADOR[c]; break;
                        case 3: nome = NOME_GOVERNADOR[c]; break;
                        default: nome = NOME_PRESIDENTE[c]; break;
                    }
                    nomesEmpatados += (candidatosEmpatados > 1 ? " e " : "") + nome;
                }
            }

            if (candidatosEmpatados > 1) {
                System.out.println("Empate! " + nomesEmpatados + " com " + maxVotos + " votos.");
            } else {
                System.out.println("Vencedor: " + nomesEmpatados + " com " + maxVotos + " votos.");
            }
        }
    }

    // --- MÉTODOS AUXILIARES ---

    // Exibe candidatos de um cargo específico
    public static void exibirCandidatosCargo(int cargo) {
        int[] numeros;
        String[] nomes;

        switch(cargo) {
            case 0: numeros = NUM_DEPUTADO_FEDERAL; nomes = NOME_DEPUTADO_FEDERAL; break;
            case 1: numeros = NUM_DEPUTADO_ESTADUAL; nomes = NOME_DEPUTADO_ESTADUAL; break;
            case 2: numeros = NUM_SENADOR; nomes = NOME_SENADOR; break;
            case 3: numeros = NUM_GOVERNADOR; nomes = NOME_GOVERNADOR; break;
            default: numeros = NUM_PRESIDENTE; nomes = NOME_PRESIDENTE; break;
        }

        System.out.println("Candidatos para " + NOMES_CARGOS[cargo] + ":");
        for (int i = 0; i < 3; i++) {
            System.out.println("  " + numeros[i] + " - " + nomes[i]);
        }
    }

    // Verifica se o número pertence ao cargo
    public static boolean isNumeroValidoCargo(int numero, int cargo) {
        int[] numerosCargo;
        switch(cargo) {
            case 0: numerosCargo = NUM_DEPUTADO_FEDERAL; break;
            case 1: numerosCargo = NUM_DEPUTADO_ESTADUAL; break;
            case 2: numerosCargo = NUM_SENADOR; break;
            case 3: numerosCargo = NUM_GOVERNADOR; break;
            default: numerosCargo = NUM_PRESIDENTE; break;
        }
        for (int n : numerosCargo) {
            if (n == numero) return true;
        }
        return false;
    }

    // Retorna o nome do candidato dado o número e o cargo
    public static String getCandidatoNome(int numero, int cargo) {
        int[] numeros;
        String[] nomes;

        switch(cargo) {
            case 0: numeros = NUM_DEPUTADO_FEDERAL; nomes = NOME_DEPUTADO_FEDERAL; break;
            case 1: numeros = NUM_DEPUTADO_ESTADUAL; nomes = NOME_DEPUTADO_ESTADUAL; break;
            case 2: numeros = NUM_SENADOR; nomes = NOME_SENADOR; break;
            case 3: numeros = NUM_GOVERNADOR; nomes = NOME_GOVERNADOR; break;
            default: numeros = NUM_PRESIDENTE; nomes = NOME_PRESIDENTE; break;
        }

        for (int i = 0; i < 3; i++) {
            if (numeros[i] == numero) {
                return nomes[i];
            }
        }
        return "Nulo/Branco";
    }

    // Atualiza os contadores globais de votos
    public static void atualizarContadoresVotos(int indexEleitor) {
        for (int cargo = 0; cargo < 5; cargo++) {
            int voto = votosEleitores[indexEleitor][cargo];
            int candidatoIndex = -1;
            switch(cargo) {
                case 0: candidatoIndex = indexOf(NUM_DEPUTADO_FEDERAL, voto); break;
                case 1: candidatoIndex = indexOf(NUM_DEPUTADO_ESTADUAL, voto); break;
                case 2: candidatoIndex = indexOf(NUM_SENADOR, voto); break;
                case 3: candidatoIndex = indexOf(NUM_GOVERNADOR, voto); break;
                default: candidatoIndex = indexOf(NUM_PRESIDENTE, voto); break;
            }
            if (candidatoIndex != -1) {
                totalVotos[cargo][candidatoIndex]++;
            }
        }
    }

    // Conta quantos eleitores votaram
    public static int countVotantes() {
        int count = 0;
        for (boolean v : eleitoresVotaram) {
            if (v) count++;
        }
        return count;
    }

    // Retorna o índice de um valor em um array de Strings, ou -1 se não encontrar
    public static int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return -1;
    }

    // Retorna o índice de um valor em um array de ints, ou -1 se não encontrar
    public static int indexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) return i;
        }
        return -1;
    }
}
