# Parte 2 – Condição de corrida e Semáforo

## 1. Condição de corrida

Na classe `CorridaSemControle`, várias threads incrementam o mesmo contador estático `count`
sem qualquer forma de sincronização. A operação `count++` não é atômica; ela é composta,
resumidamente, por:

1. Ler o valor atual de `count` da memória para um registrador.  
2. Somar 1 nesse registrador.  
3. Escrever o resultado de volta em `count`.

Quando duas ou mais threads executam essa sequência ao mesmo tempo, elas podem ler o mesmo
valor inicial antes que a gravação da outra thread aconteça, fazendo com que alguns incrementos
sejam “perdidos”. Por isso, ao final da execução, o valor observado de `count` costuma ser
menor do que o valor esperado `T × M`.

Isso caracteriza uma **condição de corrida**: o resultado depende da interleaving de execuções,
e pequenas variações de escalonamento podem alterar o valor final.

## 2. Uso do Semáforo binário

Na classe `CorridaComSemaphore`, introduzimos um `Semaphore`:

```java
static final Semaphore sem = new Semaphore(1, true);
```

- O parâmetro `1` indica que há apenas **uma permissão**, logo apenas **uma thread**
  pode entrar na seção crítica por vez.  
- O parâmetro `true` pede uma política de filas justa (FIFO), evitando que uma thread
  fique sendo sempre preterida.

Antes de executar `count++`, a thread faz:

```java
sem.acquire();   // bloqueia se outra thread estiver na seção crítica
count++;         // operação protegida
sem.release();   // libera para a próxima thread
```

Com isso, as threads passam pela operação de incremento **uma de cada vez**,
garantindo **exclusão mútua** e eliminando a condição de corrida.

## 3. Resultado esperado e discussões

- Na versão sem controle, o valor final de `count` tende a ser **< T × M** e varia a cada execução.  
- Na versão com semáforo, o valor final deve ser **exatamente T × M**, pois não há perda de incrementos.

Há um possível **trade-off de desempenho**:

- O semáforo pode reduzir o **throughput**, pois as threads passam a se serializar na seção crítica.  
- Em compensação, ganhamos **correção**, previsibilidade e uma relação bem definida de
  **ordem happens-before** entre os acessos ao contador, preservando a visibilidade das escritas
  entre as threads.
