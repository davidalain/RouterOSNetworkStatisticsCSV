# RouterOSNetworkStatisticsCSV

Programa em Java que faz a extração das informações das filas (Simple Queues) de um roteador rodando RouterOS da Mikrotik.
Estas informações são extraídas da página de gráficos que ficam hospedadas em servidor HTTP do próprio roteador em 'http://<DEVICE_ADDRESS>/graphs/'.
Este programa faz a extração de todas as filas e geração de um arquivo CSV com as informações de consumo médio de envio e recebimento (Average In / Average Out) de cada cliente em cada gráfico (Daily, Weekly, Monthly e Yearly).
