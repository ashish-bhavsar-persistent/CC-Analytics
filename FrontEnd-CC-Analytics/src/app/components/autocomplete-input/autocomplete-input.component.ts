import { Component, OnInit, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment.prod';

export interface User {
  accountId: string;
  accountName: string;
}

@Component({
  selector: 'autocomplete-input',
  templateUrl: './autocomplete-input.component.html',
  styleUrls: ['./autocomplete-input.component.scss']
})
export class AutocompleteInputComponent implements OnInit {

  @Input() placeholderData : string;
  constructor(private http: HttpClient) { }
  myControl = new FormControl();
  options: User[] = [];
  filteredOptions: Observable<User[]>;

  ngOnInit() {

    let headers = new HttpHeaders({
      Authorization: 'Bearer ' + `${sessionStorage.getItem("token")}`
    });

    this.http.get(environment.ENV.baseURL+'/api/v1/accounts/name', {headers:headers})
    .subscribe(res => {
      let temp:any = res;
      console.log(temp);
      for(let i of temp){
        this.options.push(i);
      }
    })

    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(name => name ? this._filter(name) : this.options.slice())
      );
  }

  displayFn(user?: User): string | undefined {
    return user ? user.accountName : undefined;
  }

  private _filter(name: string): User[] {
    const filterValue = name.toLowerCase();

    return this.options.filter(option => option.accountName.toLowerCase().indexOf(filterValue) === 0);
  }
}