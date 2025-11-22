# Parte 3 – Deadlock: reprodução e correção

## 1. Análise do código `DeadlockDemo`

O programa cria duas threads e dois locks (`LOCK_A` e `LOCK_B`). A lógica é:

- **Thread T1**: adquire `LOCK_A`, dorme um pouco e depois tenta adquirir `LOCK_B`.  
- **Thread T2**: adquire `LOCK_B`, dorme um pouco e depois tenta adquirir `LOCK_A`.

Um cenário típico é:

1. T1 entra em `synchronized (LOCK_A)` e segura `LOCK_A`.  
2. T2 entra em `synchronized (LOCK_B)` e segura `LOCK_B`.  
3. Depois da pausa, T1 tenta entrar em `synchronized (LOCK_B)`, mas `LOCK_B` está com T2, então T1 fica bloqueada.  
4. T2, após sua pausa, tenta entrar em `synchronized (LOCK_A)`, mas `LOCK_A` está com T1, então T2 também fica bloqueada.

Nenhuma das duas threads progride: temos um **deadlock**.

Relacionando com as **condições de Coffman**:

1. **Exclusão mútua** – cada lock só pode ser segurado por uma thread de cada vez.  
2. **Manter-e-esperar (hold and wait)** – cada thread mantém um lock enquanto espera pelo outro.  
3. **Não preempção** – os locks não são retirados à força das threads.  
4. **Espera circular** – T1 espera pelo lock de T2 e T2 espera pelo lock de T1, formando um ciclo.

Todas as condições necessárias aparecem simultaneamente, logo o sistema pode ficar parado indefinidamente.

## 2. Correção por hierarquia de recursos

Na classe `DeadlockResolvido`, ambas as threads chamam `tarefaComOrdem`, que sempre executa:

```java
synchronized (LOCK_A) {
    dormir(50);
    synchronized (LOCK_B) {
        // seção crítica
    }
}
```

Ou seja, **todas as threads adquirem os locks na mesma ordem**: primeiro `LOCK_A`, depois `LOCK_B`.
Isso impõe uma **hierarquia global** sobre os recursos, semelhante à solução da Parte 1
para o Jantar dos Filósofos.

Com essa ordem fixa:

- É impossível que uma thread segure `LOCK_B` e esteja esperando `LOCK_A` enquanto outra segura `LOCK_A`
  e espera `LOCK_B`.  
- Se uma thread está em `LOCK_B`, ela necessariamente já adquiriu `LOCK_A` antes.

Logo, a condição de **espera circular** é removida, impedindo o deadlock, embora as demais
condições (exclusão mútua, manter-e-esperar e não preempção) continuem verdadeiras.

## 3. Relação com o Jantar dos Filósofos

A ideia usada aqui é exatamente a mesma da Parte 1:

- Cada lock é visto como um recurso com uma posição na hierarquia (`LOCK_A` antes de `LOCK_B`).  
- As threads são como filósofos competindo por garfos.  
- A regra “sempre pegue primeiro o recurso de menor índice” quebra a espera circular
  e, portanto, impede o impasse.

Isso ilustra como um padrão de solução (hierarquia de recursos) pode ser reutilizado
em problemas diferentes de concorrência.
