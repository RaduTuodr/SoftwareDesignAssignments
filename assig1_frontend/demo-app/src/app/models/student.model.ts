import { Person } from "./person.model";

export interface Student extends Person {
  registrationNumber: string;
  graduationYear: number;
}

export type CreateStudentDto = Omit<Student, 'id'>;
export type UpdateStudentDto = Omit<Student, 'id'>;