# Introdução #
Este projeto foi construído no âmbito da unidade curricular de Inteligência Artificial do curso de Engenharia Informática da [Escola Superior de Tecnologia e Gestão de Leiria](http://www.estg.ipleiria.pt).

É uma aplicação que com recurso aos algoritmos minimax e alfa-beta, é capaz de jogar o jogo do Moinho ou Trilha (Nine Men's Morris). Para além de toda a lógico de jogo e mecanismos de avaliação de estados entre jogadas, o jogo implementa um sistema de geração e de verificação automática de coeficientes que quando conjugados com as respetivas funções de avaliação permitem a criação de jogadores com diferentes "graus de inteligência" para o problema em estudo.

Tecnologicamente é uma solução baseada em Java onde foi adicionada uma camada de gestão de dados baseada no motor SGBD SQLite para persistência dos dados recolhidos e análise estatística dos mesmos. O interface da aplicação é baseado em código fornecido, tenho sido este adaptado para suportar as novas funcionalidades implementadas.


---

# Screenshots #
## Interface principal da aplicação ##
<img src='http://moinho.googlecode.com/svn/wiki/images/interface.jpg' alt='Imagem da interface principal' />

## Coeficientes das funções de avaliação ##
<img src='http://moinho.googlecode.com/svn/wiki/images/coeficientes.jpg' alt='Imagem dos coeficientes' />

## Interface de treino de jogadores ##
<img src='http://moinho.googlecode.com/svn/wiki/images/modo_treino.jpg' alt='Imagem da interface de treino de jogadores' />