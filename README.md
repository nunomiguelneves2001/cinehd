# CineBox

App Android (WebView) que abre cinehd.vc, pensada para box Android com comando.

## Comandos
- Setas + OK: mexem o cursor e clicam
- Cursor perto do topo/fundo do ecrã: faz scroll
- PG ▲/▼ (ou CH): scroll de página
- Menu (☰) ou botão vermelho: liga/desliga o cursor (desligado, as setas saltam entre links)
- Voltar: página anterior / sai do vídeo em ecrã inteiro / fecha a app

## Configuração
Em `app/src/main/java/pt/cinebox/app/WebActivity.java`:
- `START_URL` e `ALLOWED_HOST`: endereço do site
- `BLOCK_EXTERNAL`: false se o site redirecionar para outro domínio e ficar em branco
