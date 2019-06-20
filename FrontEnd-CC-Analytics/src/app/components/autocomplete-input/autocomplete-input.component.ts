import { Component, OnInit, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

export interface User {
  accountName: string;
  accountId: string;
}

/**
 * @title Display value autocomplete
 */
@Component({
  selector: 'autocomplete-input',
  templateUrl: 'autocomplete-input.component.html',
  styleUrls: ['autocomplete-input.component.scss'],
})
export class AutocompleteInputComponent implements OnInit {
  @Input() placeholderData: string;
  myControl = new FormControl();
  options: User[] = [];
  filteredOptions: Observable<User[]>;

  constructor(private http: HttpClient) {

  }

  ngOnInit() {
    let headers = new HttpHeaders({
      Authorization: 'Bearer ' + `${sessionStorage.getItem("token")}`
    });

    this.http.get(environment.ENV.baseURL + '/api/v1/accounts/name', { headers: headers })
      .subscribe(res => {
        let temp:any = res;
        for (let i of temp) {
          this.options.push(i);
        }
        this.filteredOptions = this.myControl.valueChanges
          .pipe(
            startWith(''),
            // map(value => typeof value === 'string' ? value : value.name),
            // map(name => name ? this._filter(name) : this.options.slice())
            map(value => this._filter(value))
          );
      })


  }



  displayFn(user?: User): string | undefined {
    return user ? user.accountName : undefined;
  }

  private _filter(name: string): User[] {
    // const filterValue = name.toLowerCase();
    // return this.options.filter(option => option.accountName.toLowerCase().indexOf(filterValue) === 0);

    const filterValue = name.toLowerCase();
    return this.options.filter(option => option.accountName.toLowerCase().includes(filterValue));
  }
}
