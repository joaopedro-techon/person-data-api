# Roteiro — Evolução da Arquitetura de Pós-Venda

> **Apresentação técnica — 30 a 40 min**
> **Público:** Engenharia, Arquitetura, Liderança técnica

---

## Slide 1 — Capa / Gancho

**Tempo estimado:** 1 min

**Conteúdo do slide (o que aparece projetado):**
- Frase grande: **"Em 4 anos, o pós-venda cresceu. É hora da arquitetura crescer junto."**
- Subtítulo discreto: Evolução da Arquitetura de Pós-Venda — Itaú Unibanco
- Apresentador / data

**Roteiro de fala:**
Bom dia. [pausa] Em 2021, a gente desenhou a arquitetura de pós-venda que atende hoje as solicitações de manutenção de crédito do banco — amortização, cancelamento, estorno, alteração de meio de cobrança, etc. Ela cumpre o que se propôs a fazer. [pausa] Mas chegamos num ponto em que praticamente todo o nosso tempo está em sustentação. Hoje a gente não consegue mais evoluir o produto — só mantém o que existe de pé. [pausa] Nos próximos 30 minutos, eu quero mostrar três coisas: por que chegamos nesse ponto, o que estamos construindo para destravar a evolução, e o que isso muda para vocês.

**Transição:**
Antes de falar do problema, deixa eu fixar rapidamente o que é o pós-venda.

---

## Slide 2 — A promessa desta conversa

**Tempo estimado:** 1 min

**Conteúdo do slide (o que aparece projetado):**
- **1. Onde estamos** — arquitetura atual e suas dores
- **2. Para onde vamos** — nova arquitetura em dois pilares
- **3. Como chegamos lá** — migração + AI como acelerador

**Roteiro de fala:**
Esta apresentação tem três partes. Primeiro, um diagnóstico honesto de onde estamos. Segundo, a arquitetura nova — a estrutura, não os detalhes. Terceiro, como vamos migrar sem quebrar quem depende da gente, e como AI está acelerando esse caminho. No fim, vou pedir uma decisão concreta de vocês.

**Transição:**
Começando pelo começo: o que faz o pós-venda.

---

## Slide 3 — O que fazemos hoje

**Tempo estimado:** 2 min

**Conteúdo do slide (o que aparece projetado):**
- **Pós-Venda = motor de manutenção das operações de crédito**
- Exemplos: amortização, liquidação, cancelamento, estorno
- Em produção desde 2021 — hoje atendemos em média, 15000 solicitações por dia

**Roteiro de fala:**
Pós-venda é o motor que cuida de toda operação de crédito depois que o cliente já tem o produto contratado. Amortização antecipada, liquidação de parcela, cancelamento, estorno de recebimento. A arquitetura que sustenta isso nasceu no final de 2021, com um objetivo claro: orquestrar essas manutenções de forma assíncrona, com máquina de estados e fluxo event-driven. Em 4 anos, passamos de 100 solicitações/dia para 15k de solicitações/dia. [pausa] É exatamente esse crescimento que vai virar a história de hoje.

**Transição:**
Vamos olhar como isso funciona por dentro.

---

## Slide 4 — Como funciona hoje

**Tempo estimado:** 3 min

**Conteúdo do slide (o que aparece projetado):**
- Diagrama **draw.io** da arquitetura atual *(já existente)*
- Em destaque visual: **Máquina de estados → Workflow sequencial → Cliente recebe ID e aguarda**

**Roteiro de fala:**
Quando uma manutenção é solicitada, o cliente recebe um identificador e entra numa fila. Por baixo, esse identificador percorre um workflow: várias fases sequenciais, cada uma assíncrona — uma valida, outra processa, outra confirma. [aponte para o diagrama] A máquina de estados, na prática, é essa sequência de etapas que o sistema executa em background. Funcionou bem por anos. [pausa] Mas tem um detalhe: do momento em que o cliente envia até o momento em que o resultado chega, ele não vê absolutamente nada. É uma caixa-preta. Para o cliente — e, como vamos ver, para a gente também.

**Transição:**
Para vocês entenderem por que isso virou um problema, deixa eu contar uma cena que se repete.

---

## Slide 5 — A história que se repete

**Tempo estimado:** 2 min

**Conteúdo do slide (o que aparece projetado):**
Linha do tempo simples:
- **08h00** — Cliente solicita amortização
- **08h01 → ?** — Workflow entra numa fase. Sem evento. Sem alerta.
- **0850** — gerente liga reclamando
- **+ horas** — time descobre o ponto travado lendo log a log, mesh, etc (MTTR aumenta)

**Roteiro de fala:**
Cliente solicita uma amortização às oito da manhã. O sistema responde com um identificador e diz "aguarde". Em algum lugar do workflow, uma fase trava — pode ser uma chamada externa que falhou, pode ser um lock, pode ser qualquer coisa. Ninguém é avisado. Nem o cliente, nem a gente. [pausa] Algumas horas depois, o cliente liga para o gerente. O gerente abre um chamado. O chamado chega na gente. Aí começa o garimpo: ler log de cinco serviços diferentes para descobrir em qual fase a coisa parou. [pausa] Isso acontece dezenas de vezes por semana. Esse é o sintoma. Vamos para a causa.

**Transição:**
Quando a gente para para olhar a causa raiz, ela cabe em três dores.

---

## Slide 6 — As 3 dores raiz

**Tempo estimado:** 4 min

**Conteúdo do slide (o que aparece projetado):**
Três blocos lado a lado:

| Experiência | Operação | Arquitetura |
|---|---|---|
| Cliente sem visibilidade | Sem observabilidade do workflow | Grande número de projetos Java apartados |
| Polling vence eventos | Sem timeout, sem stuck detection | Disfunções de domínio + workarounds |

Frase ancorada no rodapé: **"Hoje gastamos mais tempo sustentando do que evoluindo."**

**Roteiro de fala:**
Toda a dor cabe em três caixas. [aponte] **Primeira: experiência.** O cliente, hoje, precisa adivinhar o estado da própria solicitação. O modelo é pull — ele consulta de tempos em tempos. Não tem evento proativo dizendo "estou na fase tal". Em 2025, isso não se sustenta. [pausa] **Segunda: operação.** A gente, do lado de dentro, também não enxerga. Não temos observabilidade nativa do workflow. Não temos timeout configurado por fase. Não temos detecção automática de travamento. Quando algo prende, descobrimos por chamado/incidente. [pausa] **Terceira: arquitetura.** Cada tipo de manutenção — amortização, cancelamento, estorno, alteração de meio de cobrança — vive num projeto Java separado. São 6 projetos de orquestradores. Código duplicado, governança fragmentada, e disfunções: o pós-venda acaba executando ações que deveriam estar no domínio de outro time. [pausa longa] Hoje gastamos [PLACEHOLDER: % do tempo] sustentando contra [PLACEHOLDER: %] evoluindo. A inversão precisa acontecer.

**Transição:**
Para tornar isso concreto, deixa eu mostrar o que muda na vida do cliente e na nossa quando essas três caixas são resolvidas.

---

## Slide 7 — Antes vs Depois

**Tempo estimado:** 2 min

**Conteúdo do slide (o que aparece projetado):**

| ANTES | DEPOIS |
|---|---|
| Cliente faz polling para saber o status | Cliente recebe evento a cada fase |
| Workflow trava em silêncio | Stuck detection + timeout por fase |
| 6 projetos com código duplicado | Orquestrador único, regras centralizadas |
| Disfunção de domínio é workaround | Cada responsabilidade no dono certo |
| Sustentação > Evolução | Evolução > Sustentação |

**Roteiro de fala:**
Mesma operação, dois mundos. [aponte] No mundo de hoje, o cliente faz polling, o workflow trava em silêncio, e a gente apaga incêndio. No mundo da nova arquitetura, cada fase emite evento, travamento dispara alerta, e o time volta a investir tempo em produto. [pausa] Não é refatoração. É mudança de paradigma. Vou explicar como nos próximos slides.

**Transição:**
Tudo começa por três princípios que nortearam o desenho.

---

## Slide 8 — Princípios da nova arquitetura

**Tempo estimado:** 2 min

**Conteúdo do slide (o que aparece projetado):**
- **1. Orientado a eventos** — cada fase fala
- **2. Observável por design** — falha vira alerta, não chamado
- **3. Evolutivo sem quebrar** — convivência com o legado

**Roteiro de fala:**
Três princípios, sem mais. [pausa] **Orientado a eventos**: nenhuma fase processa em silêncio — todas publicam estado, e qualquer consumidor pode se inscrever. Polling vira passado. [pausa] **Observável por design**: observabilidade não é plugin no fim; é requisito desde o primeiro slide do desenho. Travamento, latência, falha — tudo vira alerta antes de o cliente reclamar. [pausa] **Evolutivo sem quebrar**: a arquitetura nova precisa coexistir com a antiga durante a migração. Quem consome hoje não pode ser obrigado a mudar amanhã.

**Transição:**
Esses três princípios viraram um desenho. Vamos olhar.

---

## Slide 9 — A solução em uma imagem

**Tempo estimado:** 3 min

**Conteúdo do slide (o que aparece projetado):**
- Diagrama **draw.io** macro da nova arquitetura *(já existente)*
- Dois blocos centrais visualmente destacados: **Orquestrador** e **Camada de Convivência**

**Roteiro de fala:**
O que vocês veem aqui é a arquitetura nova em uma imagem. [pausa] Dois blocos centrais. O **orquestrador** — esse, no meio — é o coração: ele recebe a solicitação, conhece o workflow inteiro, dispara cada fase, escuta os eventos de retorno e mantém o estado. Centralizado, único, dono das regras. [aponte para o segundo bloco] A **camada de convivência** — aqui na borda — é o que permite essa arquitetura entrar em produção sem quebrar quem chama os serviços antigos. Ela traduz, redireciona e isola. Vou abrir cada um nos próximos slides.

**Transição:**
Começando pelo orquestrador.

---

## Slide 10 — Pilar 1: Orquestrador

**Tempo estimado:** 3 min

**Conteúdo do slide (o que aparece projetado):**
- **Orquestrador = máquina de estados unificada**
- Centraliza workflows de todas as manutenções
- Cada fase publica evento
- Sem disfunção: cada responsabilidade no domínio correto

Frase ancorada: **"Um workflow, um lugar, uma verdade."**

**Roteiro de fala:**
O orquestrador substitui os 6 projetos Java apartados por um só componente. A máquina de estados deixa de ser algo replicado por tipo de manutenção e passa a ser uma estrutura única, parametrizável. Amortização, cancelamento, estorno — todos descrevem seu workflow nele. [pausa] Cada fase, ao concluir, publica um evento. Isso resolve, de uma vez, dois problemas: o cliente passa a receber notificação proativa, e a gente passa a ter rastro completo do fluxo. [pausa] E uma decisão importante: o orquestrador não executa lógica que não é dele. O orquestrador coordena, não invade.

**Transição:**
Mas ter um orquestrador novo é metade do trabalho. A outra metade é não quebrar quem já chama a gente.

---

## Slide 11 — Pilar 2: Camada de Convivência

**Tempo estimado:** 3 min

**Conteúdo do slide (o que aparece projetado):**
- **Camada de Convivência = ponte entre o legado e o novo**
- Mantém os contratos antigos vivos
- Redireciona o tráfego para o orquestrador novo
- Permite rollout gradual e desligamento transparente

Frase ancorada: **"Evoluímos sem quebrar quem depende da gente."**

**Roteiro de fala:**
Quem hoje chama os endpoints antigos não pode ser forçado a mudar. A camada de convivência resolve isso. [pausa] Ela mantém os contratos antigos vivos, mas por baixo redireciona para o orquestrador novo. Para quem chama, nada muda — mesmo endpoint, mesmo payload, mesma resposta. Para a gente, tudo muda — o processamento já roda na arquitetura nova. [pausa] Isso permite duas coisas: rollout gradual por consumidor — a gente liga um, observa, liga o próximo —, e desligamento transparente do sistema antigo quando todos já estiverem migrados. Sem janela de migração coordenada. Sem big bang.

**Transição:**
A pergunta natural é: como essa migração acontece na prática.

---

## Slide 12 — Estratégia de Migração

**Tempo estimado:** 2 min

**Conteúdo do slide (o que aparece projetado):**
- **1. Convivência ligada** — nova arquitetura recebe parte do tráfego
- **2. Migração consumidor a consumidor** — um por vez, com observação
- **3. Desligamento do legado** — sem clientes, fora

Marcos: MVP em Julho/2026 · primeira onda em Agosto/2026

**Roteiro de fala:**
Três fases. [pausa] **Primeira**: a camada de convivência entra em produção, e a gente direciona uma fatia controlada do tráfego para o orquestrador novo. Métricas em paralelo com o legado. [pausa] **Segunda**: migração consumidor por consumidor. Cada consumidor que migra ganha os benefícios — eventos, observabilidade — sem precisar mudar o código dele. [pausa] **Terceira**: quando o último consumidor estiver no novo, o sistema antigo é desligado. [pausa] Os marcos atuais: MVP em Julho/2026 e a primeira onda de migração em Agosto/2026.

**Transição:**
Para construir tudo isso na velocidade que precisamos, AI virou parte do time.

---

## Slide 13 — AI como acelerador (Devin)

**Tempo estimado:** 2 min

**Conteúdo do slide (o que aparece projetado):**
- **Devin** — agente autônomo de engenharia de software
- Implementa código, escreve testes, navega no repositório

Frase ancorada: **"AI não substituiu engenheiros — encurtou o caminho do desenho ao código."**

**Roteiro de fala:**
Devin é um agente de engenharia de software. Ele lê o repositório, entende a estrutura, implementa funcionalidade, escreve testes, abre PR. Não é autocomplete — é um par que executa tarefa de ponta a ponta. [pausa] No MVP da nova arquitetura, ele entregou 60% do código. Isso liberou o time para gastar tempo onde o ganho é maior: decisão de design, validação de regra de negócio, revisão de código. [pausa] Sem ele, esse MVP teria levado triplo do tempo.

**Transição:**
Falamos de problema, solução, migração e ferramentas. Falta a parte mais importante: o que vocês levam daqui.

---

## Slide 14 — Conclusão e Próximos Passos

**Tempo estimado:** 2 min

**Conteúdo do slide (o que aparece projetado):**

**3 mensagens para levar:**
- A arquitetura atual chegou no limite — não é dívida, é teto
- Orquestrador + convivência resolvem sem big bang
- AI acelera, mas as decisões continuam sendo de produto e arquitetura

**O que precisamos de vocês:**
- Feedkback sincero pois o prduto não é apenas de uma squad... Ele é de toda comunidade!

**Roteiro de fala:**
Três coisas para vocês levarem. [pausa] **Um**: a arquitetura de hoje funciona, mas chegou no limite. O problema não é dívida técnica — é teto de produto. [pausa] **Dois**: a saída é um orquestrador único mais uma camada de convivência. Não é big bang, é evolução controlada. [pausa] **Três**: AI encurta o caminho, mas as decisões importantes — quais workflows migram primeiro, como tratamos disfunção de domínio, qual o contrato dos eventos — continuam sendo de vocês. [pausa] E é por isso que precisamos de duas coisas concretas saindo desta sala: [Feedkback sincero pois o prduto não é apenas de uma squad... Ele é de toda comunidade!. [pausa] Obrigado. Perguntas.

**Transição:**
*(fim da apresentação principal — abre para Q&A ou para os slides de draw.io detalhados)*

---

## Anexo — Slides com draw.io (deep dive técnico)

Material visual já preparado para uso oral em Q&A ou tempo extra.

**Sugestão de roteiro durante esses slides:**
- Destacar o caminho do evento, fase a fase, no diagrama novo
- Apontar onde estavam os pontos de falha do legado e como o desenho novo os trata
- Mostrar o ponto exato onde a camada de convivência intercepta uma chamada

---

## Checklist antes de gerar o PowerPoint

- [ ] Confirmar tempo total: ~32 min (sem Q&A)
- [ ] Validar que cada slide projetado tem no máximo 5 bullets / 6 palavras por bullet
- [ ] Ler em voz alta e cronometrar slide a slide
- [ ] Conferir se o slide 1 ainda abre com a frase provocativa, não com "Evolução da Arquitetura"
- [ ] Conferir se o slide 14 termina com ação concreta, não com agradecimento genérico
