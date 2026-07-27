import "./InfoCard.css";
function InfoCard({ titulo, valor }) {
    return (
        <div className="info-card">
            <span>
                {titulo}
            </span>
            <strong>
                {valor}
            </strong>
        </div>
    );

}

export default InfoCard;