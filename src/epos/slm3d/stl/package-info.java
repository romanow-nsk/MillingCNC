/**
 * Created by romanow on 30.11.2017.
 */
package epos.slm3d.stl;
/*
STL (от англ. stereolithography) — формат файла, широко используемый для хранения трёхмерных моделей объектов
для использования в аддитивных технологиях. информация об объекте хранится как список треугольных граней,
которые описывают его поверхность, и их нормалей. STL-файл может быть текстовым (ASCII) или двоичным.
Свое название получил от сокращения термина "Stereolithography", поскольку изначально применялся
именно в этой технологии трехмерной печати.

UINT8[80] – Header
UINT32 – Number of triangles

foreach triangle
REAL32[3] – Normal vector
REAL32[3] – Vertex 1
REAL32[3] – Vertex 2
REAL32[3] – Vertex 3
UINT16 – Attribute byte count
*/