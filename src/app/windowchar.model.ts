export class WindowChar{
	public value: string;
	public state: number;
	public row: number = 0;
	public column: number = 0;	

	constructor(value: string){
		this.value = value;
		this.state = 0;
	}
	getColor(onCursor: boolean, blocked: boolean) : string{
		if(onCursor)return '#FFF';
		switch(this.state){
			case 0: return "#000";
			case 1: {
				if(!blocked)return "#0A0";
				else return "#040";
			}
			case 2: {
				return "#A00"
			}
		}
		return "#000";
	}
}
