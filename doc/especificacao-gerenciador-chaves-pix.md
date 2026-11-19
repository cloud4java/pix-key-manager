# Especificação — Gerenciador de Chaves PIX

## Contexto e objetivo

Criar um gerenciador para clientes finais pessoas físicas e jurídicas. Como não haverá integração externa nesta versão, recomenda-se tratar o primeiro escopo como **protótipo de gestão interna**: um registro no sistema **não significa que a chave esteja registrada no DICT nem pronta para receber pagamentos**.

**Premissas propostas:** permitir cadastrar, consultar/listar e remover chaves; não editar uma chave cadastrada — para alterá-la, removê-la e criar outra. Considerar os tipos CPF, CNPJ, e-mail, telefone e chave aleatória como proposta a confirmar. Não aplicar limites de quantidade nesta versão.

## Épicos e histórias

### Épico 1 — Gerenciar registros de chaves PIX

- **H1 — Cadastrar chave**  
  Como cliente final, quero registrar uma chave no gerenciador, para organizar minhas chaves. **5 pontos** — estimativa incerta enquanto tipos e validações não forem confirmados.
- **H2 — Consultar minhas chaves**  
  Como cliente final, quero listar e consultar os detalhes das chaves registradas, para conferir meus dados. **3 pontos**.
- **H3 — Remover chave**  
  Como cliente final, quero remover uma chave do gerenciador, para manter minha lista atualizada. **3 pontos**.

### Épico 2 — Proteger dados e evitar interpretações incorretas

- **H4 — Identificar o estado do registro e proteger dados pessoais**  
  Como cliente final, quero saber que o registro é apenas interno e ver meus dados pessoais protegidos, para não confundi-lo com uma chave ativa no DICT nem expor informações desnecessariamente. **5 pontos**.

## Critérios de aceite

### H1 — Cadastrar chave

- **Dado** que o cliente está no cadastro, **quando** informar tipo e chave em formato aceito, **então** o sistema registra a chave e informa que o registro é interno, sem afirmar que houve registro no DICT.
- **Dado** que o formato informado é inválido ou falta um campo obrigatório, **quando** o cliente tentar salvar, **então** o sistema explica o erro e não cria o registro.
- **Dado** que o cadastro contém dados pessoais, **quando** a chave for exibida em telas ou mensagens, **então** os dados devem ser mascarados conforme a política de privacidade definida.

**Implementação do protótipo:** `POST /api/pix-keys` recebe `type` (`CPF`, `CNPJ`, `EMAIL`, `PHONE` ou `RANDOM`) e `value`, persiste o registro no H2 e responde com HTTP 201, valor mascarado e estado `INTERNAL_RECORD`. CPF/CNPJ são verificados pelo formato, sem cálculo dos dígitos verificadores; telefone usa validação básica de caracteres, e e-mail/chave aleatória seguem formatos sintáticos simples/UUID. Este endpoint não consulta nem registra chaves no DICT. Como ainda não há autenticação, deve ser usado somente com dados fictícios.

### H2 — Consultar minhas chaves

- **Dado** que existem registros, **quando** o cliente abrir a lista ou um detalhe, **então** verá o tipo, o valor protegido e o estado interno do registro.
- **Dado** que não existem registros, **quando** o cliente abrir a lista, **então** verá uma indicação clara de lista vazia e como iniciar um cadastro.
- **Dado** que ainda não há autenticação e controle de acesso, **quando** o protótipo for usado, **então** deverá usar somente dados fictícios; não poderá ser disponibilizado para dados reais de clientes.

### H3 — Remover chave

- **Dado** que uma chave está registrada, **quando** o cliente solicitar sua remoção e confirmar, **então** ela deixa de aparecer entre os registros ativos do gerenciador.
- **Dado** que o cliente iniciou uma remoção, **quando** cancelar a confirmação, **então** o registro permanece inalterado.
- **Dado** que a remoção falhar, **quando** o sistema informar o resultado, **então** não deverá indicar sucesso nem ocultar o erro.

### H4 — Estado e privacidade

- **Dado** que uma chave foi registrada sem consulta ao DICT, **quando** o sistema exibir seu estado, **então** deverá identificá-la como registro interno, não como chave PIX ativa.
- **Dado** que uma tela, mensagem ou comprovante exiba dados pessoais, **quando** o conteúdo for apresentado, **então** deverá aplicar mascaramento; acesso a dados reais exigirá autenticação e autorização antes de qualquer uso fora do protótipo.

## Priorização

| Prioridade | Histórias | Justificativa |
|---|---|---|
| **Must** | H1, H2, H4 | Núcleo do protótipo e prevenção de exposição ou falsa indicação de registro no DICT. |
| **Should** | H3 | Completa a gestão básica dos registros. |
| **Fora desta versão** | Integração DICT/core, autenticação e limites regulatórios/operacionais | Conforme informado; são necessários antes de operar com dados reais ou oferecer registro efetivo de chaves. |

## Riscos, premissas e perguntas em aberto

- **Risco alto:** sem autenticação, autorização e integração DICT, o produto não deve ser tratado como serviço real de gerenciamento de chaves. Usar apenas dados fictícios no protótipo.
- **Privacidade:** CPF/CNPJ, telefone e e-mail são dados pessoais. Recomenda-se mascaramento e controles de acesso como requisitos antes de qualquer piloto com clientes, ainda que estejam fora do momento atual.
- **Decisões pendentes antes da implementação:** confirmar se “ambos” significa atender PF e PJ; quais tipos de chave entram no MVP; quais validações de formato são esperadas; e se a remoção é apenas do registro interno ou deve futuramente solicitar exclusão no DICT.

## Definition of Done

- Testes unitários e de integração com `@SpringBootTest`.
- Documentação atualizada.
- Revisão de código concluída.
