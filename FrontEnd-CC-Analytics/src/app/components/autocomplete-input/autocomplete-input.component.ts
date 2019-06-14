import { Component, OnInit, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';

export interface User {
  name: string;
}

@Component({
  selector: 'autocomplete-input',
  templateUrl: './autocomplete-input.component.html',
  styleUrls: ['./autocomplete-input.component.scss']
})
export class AutocompleteInputComponent implements OnInit {

  @Input() placeholderData : string;
  constructor() { }
  myControl = new FormControl();
  options: User[] = [
    {name: 'Mary'},
    {name: 'Shelley'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'},
    {name: 'Igor'}
  ];
  filteredOptions: Observable<User[]>;

  ngOnInit() {
    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(name => name ? this._filter(name) : this.options.slice())
      );
  }

  displayFn(user?: User): string | undefined {
    return user ? user.name : undefined;
  }

  private _filter(name: string): User[] {
    const filterValue = name.toLowerCase();

    return this.options.filter(option => option.name.toLowerCase().indexOf(filterValue) === 0);
  }
}