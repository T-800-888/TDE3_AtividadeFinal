# Pseudocódigo – Jantar dos Filósofos com hierarquia de recursos

## Dados

- N = 5 filósofos (numerados de 0 a 4)
- N garfos (também numerados de 0 a 4)
- O garfo `i` fica entre o filósofo `i` e o filósofo `(i + 1) mod N`
- Cada garfo é protegido por um `lock` ou semáforo binário

```pseudo
para cada filósofo p em 0..N-1 executar concorrentemente:

    função indice_garfo_esquerda(p):
        retornar p

    função indice_garfo_direita(p):
        retornar (p + 1) mod N

    left  = min(indice_garfo_esquerda(p), indice_garfo_direita(p))
    right = max(indice_garfo_esquerda(p), indice_garfo_direita(p))

    loop infinito:
        pensar()
        estado[p] <- "com fome"

        adquirir(lock_garfo[left])      // bloqueia até o garfo de menor índice ficar livre
        adquirir(lock_garfo[right])     // bloqueia até o outro garfo ficar livre

        estado[p] <- "comendo"
        comer()

        liberar(lock_garfo[right])
        liberar(lock_garfo[left])

        estado[p] <- "pensando"
```

### Observações

- Todos os filósofos seguem a **mesma regra de ordenação**: sempre adquirem primeiro o garfo
  de menor índice (`left`) e depois o de maior índice (`right`).
- Isso impõe uma **ordem global** sobre os recursos e elimina a **espera circular**, impedindo o deadlock.
