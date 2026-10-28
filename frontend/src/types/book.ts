export type Genres = 'SCIENCE' | 'ARTISTIC' | 'MOTIVATION' | 'ROMANCE' | 'FANTASY' | 'HISTORICAL';

export type Languages = 'EN' | 'RU' | 'UZB';

export interface Book {
  id: number;
  title: string;
  content: string;
  author: string;
  publisher: string;
  publishedYear: number;
  genres: Genres;
  price: number;
  languages: Languages;
  userId: number;
  createdAt: string;
  updatedAt: string;
}

export interface BookRequest {
  title: string;
  content: string;
  author: string;
  publisher: string;
  publishedYear: number;
  price: number;
  genres: Genres;
  languages: Languages;
}

export const GENRE_LABELS: Record<Genres, string> = {
  SCIENCE: 'Ilmiy (Science)',
  ARTISTIC: 'Badiiy (Artistic)',
  MOTIVATION: 'Motivatsiya (Motivation)',
  ROMANCE: 'Romantika (Romance)',
  FANTASY: 'Fantastika (Fantasy)',
  HISTORICAL: 'Tarixiy (Historical)',
};

export const LANGUAGE_LABELS: Record<Languages, string> = {
  UZB: "O'zbekcha",
  EN: 'Inglizcha',
  RU: 'Ruscha',
};
