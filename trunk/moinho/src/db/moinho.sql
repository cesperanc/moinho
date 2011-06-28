-- Table: pesos_tbl
CREATE TABLE pesos_tbl ( 
    pesoId                INTEGER         PRIMARY KEY AUTOINCREMENT,
    a1                    NUMERIC         DEFAULT ( 0 ),
    a2                    NUMERIC         DEFAULT ( 0 ),
    a3                    NUMERIC         DEFAULT ( 0 ),
    a4                    NUMERIC         DEFAULT ( 0 ),
    a5                    NUMERIC         DEFAULT ( 0 ),
    a6                    NUMERIC         DEFAULT ( 0 ),
    a7                    NUMERIC         DEFAULT ( 0 ),
    profundidade_pesquisa NUMERIC         DEFAULT ( 3 ),
    algoritmo             VARCHAR( 250 )  NOT NULL,
    CONSTRAINT 'unique_peso' UNIQUE ( a1, a2, a3, a4, a5, a6, a7, a8, a9, profundidade_pesquisa, algoritmo )  ON CONFLICT ROLLBACK 
);
INSERT INTO [pesos_tbl] ([pesoId], [a1], [a2], [a3], [a4], [a5], [a6], [a7], [a8], [a9], [profundidade_pesquisa], [algoritmo]) VALUES (1, 10, 9, 8, 7, 6, 5, 4, 2, 'AlfaBeta');
INSERT INTO [pesos_tbl] ([pesoId], [a1], [a2], [a3], [a4], [a5], [a6], [a7], [a8], [a9], [profundidade_pesquisa], [algoritmo]) VALUES (2, 9, 8, 7, 6, 5, 4, 3, 3, 'MiniMax');
INSERT INTO [pesos_tbl] ([pesoId], [a1], [a2], [a3], [a4], [a5], [a6], [a7], [a8], [a9], [profundidade_pesquisa], [algoritmo]) VALUES (3, 11, 10, 9, 8, 7, 6, 5, 4, 'AlfaBeta');

-- Table: jogos_tbl
CREATE TABLE jogos_tbl ( 
    jogoId        INTEGER PRIMARY KEY AUTOINCREMENT,
    jogador1Id    INTEGER NOT NULL
                          REFERENCES pesos_tbl ( pesoId ) ON DELETE CASCADE
                                                          ON UPDATE CASCADE,
    jogador2Id    INTEGER NOT NULL
                          REFERENCES pesos_tbl ( pesoId ) ON DELETE CASCADE
                                                          ON UPDATE CASCADE,
    vencedorId    INTEGER REFERENCES pesos_tbl ( pesoId ) ON DELETE CASCADE
                                                          ON UPDATE CASCADE,
    vencidoId     INTEGER REFERENCES pesos_tbl ( pesoId ) ON DELETE CASCADE
                                                          ON UPDATE CASCADE,
    numeroJogadas NUMERIC DEFAULT ( 0 ),
    duracaoJogo   NUMERIC DEFAULT ( 0 ),
    UNIQUE ( jogador1Id, jogador2Id, vencedorId, vencidoId, numeroJogadas, duracaoJogo )  ON CONFLICT REPLACE 
);
INSERT INTO [jogos_tbl] ([jogoId], [jogador1Id], [jogador2Id], [vencedorId], [vencidoId], [numeroJogadas], [duracaoJogo]) VALUES (4, 1, 2, 2, 1, 25, 5);
INSERT INTO [jogos_tbl] ([jogoId], [jogador1Id], [jogador2Id], [vencedorId], [vencidoId], [numeroJogadas], [duracaoJogo]) VALUES (5, 1, 3, 1, 3, 0, 0);
INSERT INTO [jogos_tbl] ([jogoId], [jogador1Id], [jogador2Id], [vencedorId], [vencidoId], [numeroJogadas], [duracaoJogo]) VALUES (6, 2, 3, 2, 3, 0, 0);
INSERT INTO [jogos_tbl] ([jogoId], [jogador1Id], [jogador2Id], [vencedorId], [vencidoId], [numeroJogadas], [duracaoJogo]) VALUES (7, 3, 2, 2, 3, 0, 0);
INSERT INTO [jogos_tbl] ([jogoId], [jogador1Id], [jogador2Id], [vencedorId], [vencidoId], [numeroJogadas], [duracaoJogo]) VALUES (8, 3, 1, 3, 1, 0, 0);
INSERT INTO [jogos_tbl] ([jogoId], [jogador1Id], [jogador2Id], [vencedorId], [vencidoId], [numeroJogadas], [duracaoJogo]) VALUES (9, 2, 1, 1, 2, 0, 0);

-- Trigger: insertVencido
CREATE TRIGGER insertVencido
       AFTER INSERT ON jogos_tbl
       WHEN new.vencidoId IS NULL
BEGIN
    UPDATE jogos_tbl
       SET vencidoId = ( 
               SELECT COALESCE( ( 
                          SELECT jogador2Id
                            FROM jogos_tbl
                           WHERE jogoId = new.jogoId 
                                 AND
                                 vencedorId = jogador1Id 
                      ) 
                      ,  ( 
                          SELECT jogador1Id
                            FROM jogos_tbl
                           WHERE jogoId = new.jogoId 
                                 AND
                                 vencedorId = jogador2Id 
                      ) 
                       ) 
           )
     WHERE jogoId = new.jogoId;
END;
;

-- Trigger: updateVencido
CREATE TRIGGER updateVencido
       AFTER UPDATE ON jogos_tbl
       WHEN new.vencedorId = new.vencidoId 
AND
new.vencedorId IS NOT NULL
BEGIN
    UPDATE jogos_tbl
       SET vencidoId = ( 
               SELECT COALESCE( ( 
                          SELECT jogador2Id
                            FROM jogos_tbl
                           WHERE jogoId = new.jogoId 
                                 AND
                                 vencedorId = jogador1Id 
                      ) 
                      ,  ( 
                          SELECT jogador1Id
                            FROM jogos_tbl
                           WHERE jogoId = new.jogoId 
                                 AND
                                 vencedorId = jogador2Id 
                      ) 
                       ) 
           )
     WHERE jogoId = new.jogoId;
END;
;

-- View: pesosView
CREATE VIEW pesosView AS
       SELECT *,
              round( COALESCE( CAST ( total_vitorias AS REAL ) / CAST ( total_jogos AS REAL ) , 0 ) * 100 ) AS percentagem_vitorias,
              round( COALESCE( CAST ( total_derrotas AS REAL ) / CAST ( total_jogos AS REAL ) , 0 ) * 100 ) AS percentagem_derrotas
         FROM  ( 
       SELECT *,
              ( total_jogadas_vitoria_como_j1 + total_jogadas_vitoria_como_j2 ) AS total_jogadas_vitoria,
              ( total_jogadas_derrota_como_j1 + total_jogadas_derrota_como_j2 ) AS total_jogadas_derrota,
              ( total_duracao_vitoria_como_j1 + total_duracao_vitoria_como_j2 ) AS total_duracao_vitoria,
              ( total_duracao_derrota_como_j1 + total_duracao_derrota_como_j2 ) AS total_duracao_derrota,
              ( total_vitorias_como_j1 + total_vitorias_como_j2 ) AS total_vitorias,
              ( total_derrotas_como_j1 + total_derrotas_como_j2 ) AS total_derrotas,
              ( total_jogos_como_j1 + total_jogos_como_j2 ) AS total_jogos
         FROM  ( 
       SELECT *,
              COALESCE( j1_1.total, 0 ) AS total_vitorias_como_j1,
              COALESCE( j1_2.total, 0 ) AS total_vitorias_como_j2,
              COALESCE( j2_1.total, 0 ) AS total_derrotas_como_j1,
              COALESCE( j2_2.total, 0 ) AS total_derrotas_como_j2,
              COALESCE( j3_1.total, 0 ) AS total_jogos_como_j1,
              COALESCE( j3_2.total, 0 ) AS total_jogos_como_j2,
              COALESCE( j4_1.total, 0 ) AS total_jogadas_vitoria_como_j1,
              COALESCE( j4_2.total, 0 ) AS total_jogadas_vitoria_como_j2,
              COALESCE( j5_1.total, 0 ) AS total_jogadas_derrota_como_j1,
              COALESCE( j5_2.total, 0 ) AS total_jogadas_derrota_como_j2,
              COALESCE( j6_1.total, 0 ) AS total_duracao_vitoria_como_j1,
              COALESCE( j6_2.total, 0 ) AS total_duracao_vitoria_como_j2,
              COALESCE( j7_1.total, 0 ) AS total_duracao_derrota_como_j1,
              COALESCE( j7_2.total, 0 ) AS total_duracao_derrota_como_j2
         FROM pesos_tbl AS p
              LEFT JOIN  ( 
       SELECT jogador1Id,
              vencedorId,
              COUNT( * ) AS total
         FROM jogos_tbl
        WHERE jogador1Id = vencedorId
        GROUP BY vencedorId,
                 jogador1Id 
               ) 
               AS j1_1
                     ON j1_1.jogador1Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador2Id,
              vencedorId,
              COUNT( * ) AS total
         FROM jogos_tbl
        WHERE jogador2Id = vencedorId
        GROUP BY vencedorId,
                 jogador2Id 
               ) 
               AS j1_2
                     ON j1_2.jogador2Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador1Id,
              vencidoId,
              COUNT( * ) AS total
         FROM jogos_tbl
        WHERE jogador1Id = vencidoId
        GROUP BY vencidoId,
                 jogador1Id 
               ) 
               AS j2_1
                     ON j2_1.jogador1Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador2Id,
              vencidoId,
              COUNT( * ) AS total
         FROM jogos_tbl
        WHERE jogador2Id = vencidoId
        GROUP BY vencidoId,
                 jogador2Id 
               ) 
               AS j2_2
                     ON j2_2.jogador2Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador1Id,
              COUNT( * ) AS total
         FROM jogos_tbl
        GROUP BY jogador1Id 
               ) 
               AS j3_1
                     ON j3_1.jogador1Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador2Id,
              COUNT( * ) AS total
         FROM jogos_tbl
        GROUP BY jogador2Id 
               ) 
               AS j3_2
                     ON j3_2.jogador2Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador1Id,
              SUM( numeroJogadas ) AS total
         FROM jogos_tbl
        WHERE jogador1Id = vencedorId
        GROUP BY jogador1Id 
               ) 
               AS j4_1
                     ON j4_1.jogador1Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador2Id,
              SUM( numeroJogadas ) AS total
         FROM jogos_tbl
        WHERE jogador2Id = vencedorId
        GROUP BY jogador2Id 
               ) 
               AS j4_2
                     ON j4_2.jogador2Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador1Id,
              SUM( numeroJogadas ) AS total
         FROM jogos_tbl
        WHERE jogador1Id = vencidoId
        GROUP BY jogador1Id 
               ) 
               AS j5_1
                     ON j5_1.jogador1Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador2Id,
              SUM( numeroJogadas ) AS total
         FROM jogos_tbl
        WHERE jogador2Id = vencidoId
        GROUP BY jogador2Id 
               ) 
               AS j5_2
                     ON j5_2.jogador2Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador1Id,
              SUM( duracaoJogo ) AS total
         FROM jogos_tbl
        WHERE jogador1Id = vencedorId
        GROUP BY jogador1Id 
               ) 
               AS j6_1
                     ON j6_1.jogador1Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador2Id,
              SUM( duracaoJogo ) AS total
         FROM jogos_tbl
        WHERE jogador2Id = vencedorId
        GROUP BY jogador2Id 
               ) 
               AS j6_2
                     ON j6_2.jogador2Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador1Id,
              SUM( duracaoJogo ) AS total
         FROM jogos_tbl
        WHERE jogador1Id = vencidoId
        GROUP BY jogador1Id 
               ) 
               AS j7_1
                     ON j7_1.jogador1Id = p.pesoId
              LEFT JOIN  ( 
       SELECT jogador2Id,
              SUM( duracaoJogo ) AS total
         FROM jogos_tbl
        WHERE jogador2Id = vencidoId
        GROUP BY jogador2Id 
               ) 
               AS j7_2
                     ON j7_2.jogador2Id = p.pesoId
        GROUP BY p.pesoId 
           ) 
           AS s1 
       ) 
       AS s2
        ORDER BY percentagem_vitorias DESC,
                  percentagem_derrotas ASC;
;


