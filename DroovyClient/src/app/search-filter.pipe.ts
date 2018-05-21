import { Pipe, PipeTransform } from '@angular/core';
import {FileDroovy} from "./request.service";

@Pipe({
  name: 'searchFilter'
})
export class SearchFilterPipe implements PipeTransform {


  transform(items: FileDroovy[], searchText: string): any[] {
    if(!items) return [];
    if(!searchText) return items;
    searchText = searchText.toLowerCase();
    return items.filter( it => {
      return it.name.toLowerCase().includes(searchText);
    });
  }
}
