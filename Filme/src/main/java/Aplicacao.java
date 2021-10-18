import org.w3c.dom.ls.LSOutput;

import javax.print.DocFlavor;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class Aplicacao {
    public static void main(String[] args) {

        System.out.println(System.getProperty("user.home"));

        List<Ator> atores = createActorList();
        List<Ator> atrizes = createActressList();
        List<Ator> todos = createAllList();


        atorMaisJovem(atores);
        apareceMais(atrizes);
        apareceMais(intervaloDeIdade(20, 30, atrizes));
        ganhouMaisDeUmaVez(todos);
        verificaAtor(todos, "Tom Hanks");
        verificaAtor(todos, "Katharine Hepburn");

    }

    private static void atorMaisJovem(List<Ator> lista) {
        lista.stream()
                .min(Comparator.comparingInt(Ator::getIdade))
                .ifPresent(ator -> System.out.println("Ator mais jovem a ganhar um oscar: " + ator.getNome() + "\n"));
    }

    private static void verificaAtor(List<Ator> lista, String nome){
        System.out.println(nome + " ganhou " + lista.stream()
                .filter(p -> p.getNome().equals(nome))
                .count() +
                " oscares: ");
        lista.stream()
                .filter(p -> p.getNome().equals(nome))
                .forEach(p -> System.out.println("em " + p.getAno() + " com " + p.getIdade() + " anos no filme " + p.getFilme()));
        System.out.println();
    }

    private static List<Ator> intervaloDeIdade(int menor, int maior, List<Ator> lista) {
        List<Ator> filtrado = lista.stream()
                .filter(p -> p.getIdade() > menor)
                .filter(p -> p.getIdade() < maior)
                .collect(toList());
        System.out.print("Entre " + menor + " e " + maior + " anos, ");
        return filtrado;
    }

    private static void apareceMais(List<Ator> lista) {
        int contador = 0;
        int max = 0;
        String nome = null;
        String nomeMax = null;
        List<String> ordenado = lista.stream()
                .map(Ator::getNome)
                .sorted()
                .collect(toList());
        for (int i = 0; i < ordenado.size(); i++) {
            if (i != 0) {
                if (Objects.equals(ordenado.get(i - 1), ordenado.get(i))) {
                    nome = ordenado.get(i);
                    contador++;
                }
                if (!Objects.equals(ordenado.get(i - 1), ordenado.get(i))) {
                    if ( contador > max) {
                        max = contador;
                        contador = 0;
                        nomeMax = nome;
                    }
                }
            }
            else {
                nome = ordenado.get(i);
                contador++;
            }
        }
        System.out.println("Atriz que mais ganhou oscar: " + nomeMax + "\n");
    }

    private static void ganhouMaisDeUmaVez(List<Ator> lista) {
        List<String> nomes = new ArrayList<>();
        List<String> ordenado = lista.stream()
                .map(Ator::getNome)
                .sorted()
                .collect(toList());
        for (int i = 0; i < ordenado.size(); i++) {
            if (i != 0) {
                if (Objects.equals(ordenado.get(i - 1), ordenado.get(i)) && !nomes.contains(ordenado.get(i))) {
                    nomes.add(ordenado.get(i));
                }
            }
        }
        System.out.println("Atores e atrizes que ganharam o oscar mais de 1 vez: ");
        nomes.forEach(System.out::println);
        System.out.println();
    }

    private static List<Ator> createActressList() {
        List<Ator> atores = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(String.valueOf(getFileFromResource("oscar_age_female.csv"))))) {
            addActressList(atores, stream);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return atores;
    }

    private static List<Ator> createAllList() {
        List<Ator> atores = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(String.valueOf(getFileFromResource("oscar_age_female.csv"))))) {
            addActressList(atores, stream);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        try (Stream<String> stream = Files.lines(Paths.get(String.valueOf(getFileFromResource("oscar_age_male.csv"))))) {
            addActorList(atores, stream);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return atores;
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = Aplicacao.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }

    }

    private static List<Ator> createActorList() {
        List<Ator> atores = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(String.valueOf(getFileFromResource("oscar_age_male.csv"))))) {
            addActorList(atores, stream);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return atores;
    }

    private static void addActorList(List<Ator> atores, Stream<String> stream) {
        List<String> lines = stream.collect(toList());
        for (int i = 1; i < lines.size(); i++) {
            String[] split = lines.get(i).split("; ");
            atores.add(new Ator(split[3],
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]),
                    split[4],
                    Sex.MALE));
        }
    }

    private static void addActressList(List<Ator> atores, Stream<String> stream) {
        List<String> lines = stream.collect(toList());
        for (int i = 1; i < lines.size(); i++) {
            String[] split = lines.get(i).split("; ");
            atores.add(new Ator(split[3],
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]),
                    split[4],
                    Sex.FEMALE));
        }
    }

}
