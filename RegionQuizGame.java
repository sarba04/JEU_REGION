import java.io.*;
import java.util.*;
import java.time.Duration;
import java.time.Instant;

class Region {
    String name;
    String capital;

    Region(String name, String capital) {
        this.name = name;
        this.capital = capital;
    }
}

class PlayerScore implements Comparable<PlayerScore> {
    String pseudo;
    double score;

    PlayerScore(String pseudo, double score) {
        this.pseudo = pseudo;
        this.score = score;
    }

    @Override
    public int compareTo(PlayerScore other) {
        return Double.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return pseudo + " - " + score;
    }
}

public class RegionQuizGame {
    private static final Region[] regions = {
            new Region("Agnéby-Tiassa", "Agboville"),
            new Region("Bafing", "Touba"),
            new Region("Bagoué", "Boundiali"),
            new Region("Bélier", "Yamoussoukro"),
            new Region("Béré", "Mankono"),
            new Region("Bounkani", "Bouna"),
            new Region("Cavally", "Guiglo"),
            new Region("Folon", "Minignan"),
            new Region("Gbêkê", "Bouaké"),
            new Region("Gbôklé", "Sassandra"),
            new Region("Gôh", "Gagnoa"),
            new Region("Gontougo", "Bondoukou"),
            new Region("Grands Ponts", "Dabou"),
            new Region("Guémon", "Duékoué"),
            new Region("Hambol", "Katiola"),
            new Region("Indénié-Djuablin", "Abengourou"),
            new Region("Iffou", "Daoukro"),
            new Region("Kabadougou", "Odienné"),
            new Region("Lôh-Djiboua", "Divo"),
            new Region("Marahoué", "Bouaflé"),
            new Region("Mé", "Adzopé"),
            new Region("Moronou", "Bongouanou"),
            new Region("Nawa", "Soubré"),
            new Region("Poro", "Korhogo"),
            new Region("San-Pédro", "San-Pédro"),
            new Region("Sud-Comoé", "Aboisso"),
            new Region("Tchologo", "Ferkessédougou"),
            new Region("Tonkpi", "Man"),
            new Region("Worodougou", "Séguéla"),
            new Region("Yamoussoukro", "Yamoussoukro"),
            new Region("Zanzan", "Bondoukou")
    };

    private static final String SCORES_FILE = "highscores.txt";
    private static List<PlayerScore> highScores = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        loadHighScores();

        System.out.print("Entrez votre pseudo: ");
        String pseudo = scanner.nextLine();

        boolean playAgain;
        do {
            displayHighScores();
            double score = playGame(scanner);

            highScores.add(new PlayerScore(pseudo, score));
            Collections.sort(highScores);
            saveHighScores();

            System.out.println("Votre score est de : " + score);

            System.out.print("Voulez-vous rejouer? (oui/non): ");
            playAgain = scanner.nextLine().equalsIgnoreCase("oui");

        } while (playAgain);

        scanner.close();
    }

    private static void loadHighScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length == 2) {
                    highScores.add(new PlayerScore(parts[0], Double.parseDouble(parts[1])));
                }
            }
            Collections.sort(highScores);
        } catch (IOException e) {
            System.out.println("Aucun score enregistré trouvé.");
        }
    }

    private static void saveHighScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORES_FILE))) {
            for (int i = 0; i < Math.min(3, highScores.size()); i++) {
                PlayerScore ps = highScores.get(i);
                writer.write(ps.pseudo + " - " + ps.score);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de l'enregistrement des scores.");
        }
    }

    private static void displayHighScores() {
        System.out.println("=== Meilleurs Scores ===");
        for (int i = 0; i < Math.min(3, highScores.size()); i++) {
            PlayerScore ps = highScores.get(i);
            System.out.println((i + 1) + ". " + ps);
        }
        System.out.println("========================");
    }

    private static double playGame(Scanner scanner) {
        List<Region> questionList = new ArrayList<>(Arrays.asList(regions));
        Collections.shuffle(questionList);

        double score = 0;
        for (int i = 0; i < 10; i++) {
            Region region = questionList.get(i);
            System.out.println("Quel est le chef-lieu de la région " + region.name + "?");
            Instant start = Instant.now();
            String answer = scanner.nextLine();
            Instant end = Instant.now();
            long timeElapsed = Duration.between(start, end).getSeconds();

            if (answer.equalsIgnoreCase(region.capital)) {
                double penalty = 0;
                if (timeElapsed > 0 && timeElapsed <= 30) {
                    penalty = 0;
                } else if (timeElapsed > 30 && timeElapsed <= 40) {
                    penalty = 0.5;
                } else if (timeElapsed > 40 && timeElapsed <= 50) {
                    penalty = 1;
                } else if (timeElapsed > 50 && timeElapsed <= 60) {
                    penalty = 1.5;
                }
                score += 2 - penalty;

                if (penalty > 0) {
                    System.out.println("Temps dépassé: Vous avez obtenu " + (2 - penalty) + " point(s).");
                } else {
                    System.out.println("Bravo! Vous avez obtenu 2 points.");
                }
            } else {
                System.out.println("Non, la bonne réponse est " + region.capital);
            }
        }
        return score;
    }
}
