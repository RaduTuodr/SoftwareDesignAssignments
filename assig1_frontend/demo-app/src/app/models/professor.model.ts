import { Person } from "./person.model";

export interface Professor extends Person {
    department: string;
    academicRank: string;
}

export type CreateProfessorDto = Omit<Professor, 'id'>;
export type UpdateProfessorDto = Omit<Professor, 'id'>;