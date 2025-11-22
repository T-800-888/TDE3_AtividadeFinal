# Parte 1 – Jantar dos Filósofos (N = 5)

## 1. Descrição do problema

O problema do Jantar dos Filósofos modela cinco filósofos sentados ao redor de uma mesa circular.  
Cada filósofo alterna entre dois tipos de atividade:

- **Pensar**  
- **Comer**

Para comer, o filósofo precisa de **dois garfos**: o da sua esquerda e o da sua direita.  
Os garfos ficam entre os filósofos, logo cada garfo é compartilhado por dois vizinhos.

Quando implementamos esse cenário em um sistema concorrente, os garfos são os **recursos compartilhados**
e os filósofos são as **threads/processos** que precisam adquirir dois recursos para entrar na
seção crítica “comendo”. Estamos interessados principalmente em:

- **Exclusão mútua** (um garfo só pode ser usado por um filósofo por vez)  
- **Ausência de impasse (deadlock)**  
- **Ausência de inanição (starvation)**  
- **Progresso/justiça** (todos eventualmente comem)  

## 2. Protocolo ingênuo e surgimento de impasse

Um protocolo simples seria:

1. Quando o filósofo fica com fome, ele pega **primeiro o garfo da esquerda**, depois o da direita.  
2. Quando termina de comer, ele devolve os dois garfos e volta a pensar.

Esse protocolo é correto do ponto de vista de **exclusão mútua** (se só usamos um `lock` por garfo),  
mas pode gerar **impasse**. Imagine a seguinte situação com N = 5:

1. Todos os filósofos estão pensando.  
2. Em um determinado momento, todos ficam com fome quase ao mesmo tempo.  
3. Cada filósofo consegue pegar o garfo da esquerda, mas o garfo da direita já está com o vizinho.  
4. Nenhum deles solta o garfo que tem, pois todos estão esperando o outro garfo para comer.

Temos então um **ciclo de espera circular**, em que cada filósofo:
- segura um recurso (um garfo) e
- espera por outro recurso (o garfo do vizinho) que nunca fica livre.

Relacionando com as **condições de Coffman** para deadlock:

1. **Exclusão mútua** – os garfos não podem ser compartilhados simultaneamente.  
2. **Manter-e-esperar (hold and wait)** – cada filósofo mantém um garfo enquanto espera o outro.  
3. **Não preempção** – o garfo não pode ser tomado à força de um filósofo.  
4. **Espera circular** – há um ciclo fechado de filósofos, cada um esperando o recurso do próximo.

O protocolo ingênuo satisfaz essas quatro condições ao mesmo tempo, portanto o sistema pode
chegar a um estado de deadlock.

## 3. Estratégia para evitar impasse: hierarquia de recursos

Uma forma clássica de evitar o deadlock é **quebrar a espera circular**.  
Fazemos isso impondo **uma ordem global sobre os recursos** e obrigando todos a respeitar essa ordem.

Neste trabalho, a ideia é:

- Dar um **índice fixo para cada garfo** (0, 1, 2, 3, 4).  
- Cada filósofo só pode adquirir os garfos seguindo a ordem de índices:
  - primeiro o garfo de **menor índice**
  - depois o garfo de **maior índice**

Não importa se para um filósofo o garfo de menor índice está à esquerda ou à direita;  
o importante é seguir a **ordem global** entre recursos.

### Por que isso elimina o deadlock?

A condição de espera circular exige um ciclo em que cada processo espera por um recurso
que está “mais à frente” na cadeia de dependências. Quando todos os processos adquirem
recursos **apenas em ordem crescente**, não é possível formar um ciclo:

- se um filósofo está segurando o garfo de índice 1 e aguardando o garfo de índice 3,  
  ninguém pode, ao mesmo tempo, segurar o garfo 3 e estar esperando pelo garfo 1,  
  pois isso violaria a regra da ordem crescente.

Logo, **a condição de espera circular é removida**, e sem ela o deadlock não pode ocorrer.
As demais condições de Coffman ainda valem (especialmente exclusão mútua e não preempção),
mas basta quebrar **uma** das quatro para impedir o impasse.

### Justiça e progresso

A hierarquia de recursos sozinha garante ausência de deadlock, mas não resolve totalmente
problemas de **inanição** (um filósofo que é sempre “menos sortudo” pode ficar muito tempo sem comer).  
Para reduzir esse risco na implementação real, pode-se combinar a hierarquia com:

- alguma forma de **agendamento justo**, fila de espera ou semáforo “fair”  
- ou um árbitro (por exemplo, um “garçom” que controla quantos podem tentar comer simultaneamente).

Para os objetivos desta atividade, porém, o foco principal é mostrar:

- como o protocolo ingênuo leva a impasse;  
- como a hierarquia de recursos remove a espera circular e, portanto, o deadlock.
