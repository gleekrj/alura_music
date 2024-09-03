package br.com.alura.screensound.principal;

import br.com.alura.screensound.model.Artista;
import br.com.alura.screensound.model.Musica;
import br.com.alura.screensound.model.TipoArtista;
import br.com.alura.screensound.repository.ArtistaRepository;
import br.com.alura.screensound.service.ConsultaChatGPT;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private ArtistaRepository repositorio;

    Scanner leitura = new Scanner(System.in);

    public Principal(ArtistaRepository repositorio){
        this.repositorio = repositorio;
    }

    public void exibeMenu() {

        var opcao = -1;

        while (opcao != 9){
            var menu = """
                    *** Screen Sound Músicas ***
                    
                    1 - Cadastrar artistas
                    2 - Cadastrar músicas
                    3 - Listar músicas
                    4 - Buscar músicas por artistas
                    5 - Pesquisar dados sobre um artista
                    
                    9 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    cadastrarArtista();
                    break;
                case 2:
                    cadastrarMusicas();
                    break;
                case 3:
                    listarMusicas();
                    break;
                case 4:
                    buscarMusicasPorArtistas();
                    break;
                case 5:
                    pesquisarDadosPorArtista();
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }


    private void cadastrarArtista() {
        var cadastrarNovo = "S";

        while (cadastrarNovo.equalsIgnoreCase("s")) {
            System.out.println("Informe o nome desse artista:");
            var nome = leitura.nextLine();
            System.out.println("Informe o tipo desse artista (Solo/Banda/Dupla):");
            var tipo = leitura.nextLine();

            TipoArtista tipoArtista = TipoArtista.converteTipo(tipo);
            Artista novoArtista = new Artista(nome, tipoArtista);

            repositorio.save(novoArtista);
            System.out.println("Novo artista cadastrado - " + novoArtista.getNome());
            System.out.println("Cadastrar novo artista? (S/N)");
            cadastrarNovo = leitura.nextLine();
        }
    }

    private void cadastrarMusicas() {
        listarArtistas();
        System.out.println("Cadastrar músicas de que artista?");
        var nome = leitura.nextLine();
        Optional<Artista> artistaBuscado = repositorio.findByNomeContainingIgnoreCase(nome);
        if (artistaBuscado.isPresent()) {
            var artista =  artistaBuscado.get();
            System.out.println("Digite o nome da música do(a) " + artista.getNome());
            var nomeMusica = leitura.nextLine();
            Musica musica = new Musica(nomeMusica, artista);
            artista.getMusicas().add(musica);
            repositorio.save(artista);
            System.out.println("Música + " + musica.getTitulo() + " do(a) " + artista.getNome() + " gravada com sucesso!");
        } else {
            System.out.println("Artista não encontrado");
        }
    }

    private void listarArtistas(){
        List<Artista> artistas = repositorio.listarArtistas();

        artistas.forEach(a -> System.out.println(a.getNome() + " - " + a.getTipoArtista()));
    }

    private void listarMusicas() {
        List<Artista> artistas = repositorio.findAll();
        artistas.forEach(a -> a.getMusicas().forEach(System.out::println));
    }

    private void buscarMusicasPorArtistas() {
        System.out.println("Buscar músicas de que artista?");
        var nomeArtista = leitura.nextLine();
        List<Musica> musicas = repositorio.listarMusicasPorArtista(nomeArtista);
        musicas.forEach(System.out::println);
    }

    private void pesquisarDadosPorArtista() {
        System.out.println("Pesquisar dados sobre qual artista?");
        var nome = leitura.nextLine();
        var resposta = ConsultaChatGPT.obterInformacao(nome);
        System.out.println(resposta.trim());
    }
}