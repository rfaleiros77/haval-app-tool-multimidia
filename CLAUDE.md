# haval — instruções do projeto

Ferramentas para o sistema multimídia do Haval (GWM). **Fork de estudo** de
`bobaoapae/haval-tool`, clonado para o GitHub do Rogério em julho/2026 —
*"clone esse repositório para um local meu em caso de ser fechado"*.

**Leia primeiro**: `README.md` (o disclaimer, que importa aqui) e `DOCS/` — a
documentação de arquitetura veio do upstream e é boa; não reescrever.

## O que é seu e o que é de terceiro

A maior parte deste repo **não é trabalho do Rogério**. Duas contribuições são:

| Commit | O que é |
|---|---|
| `37c0310` | ação de volante para abrir a tela de gráficos no cluster |
| `6b76685` | gerenciador de eSIM + script de diagnóstico (`DOCS/esim-diagnostico.sh`) |

Ao mexer, deixar claro nos commits o que é contribuição própria — o resto tem
outra autoria e outra licença.

## Este projeto é engenharia reversa declarada

O `README.md` abre com um disclaimer em inglês e português: iniciativa
educacional não oficial, sem vínculo com a Haval/GWM, sem fim comercial, o uso
pode violar os termos do fabricante.

- **Manter o disclaimer** em qualquer redistribuição ou fork.
- **Não distribuir material original protegido** do fabricante.
- É um carro: mudança no cluster e no multimídia tem risco físico e de garantia
  — a decisão de instalar é sempre do Rogério, nunca da sessão.

## Estado — parado por decisão

O trabalho foi **interrompido de propósito**: *"vamos continuar depois",
"depois voltamos para esse assunto"*. Não é abandono; é uma fila.

O código vive na branch **`feature/acao-volante-abrir-graficos`**, que
**nunca foi publicada** — o repo `tontonhaval/haval-tool` de onde veio é de
outra pessoa e o Rogério não tem permissão de push lá (403 confirmado em
11/08/2026).

## Pendências conhecidas

- A documentação original está num Google Docs **bloqueado** para leitura
  automática. A alternativa levantada na época foi transcrever pela tela, à
  mão. Não foi feito.
- O `.claude/launch.json` (Vite, porta 1420) foi criado em 11/08 e está
  **commitado só localmente** — o push falha por falta de acesso ao repo de
  origem.

Idioma: conversa em PT-BR; o código e os commits do upstream são em inglês/PT
misturados — seguir o estilo do arquivo que estiver editando.
