import "./SearchBar.css";
import { Search } from "lucide-react";

function SearchBar({ value = "", onChange }) {
    return (
        <div className="search-container">
            <Search
                size={18}
                className="search-icon"
            />
            <input
                className="search-bar"
                type="text"
                placeholder="Buscar por nombre..."
                value={value}
                onChange={onChange}
            />
        </div>
    );

}

export default SearchBar;